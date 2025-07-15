package com.github.wzhiyog.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GradientExecutor {
    // 全局线程池配置
    private static final int GLOBAL_MAX_THREADS = 4096;
    private static final int LOW_PRIORITY_POOL_SIZE = 64;

    // 执行器实例
    private static final GradientExecutor INSTANCE = new GradientExecutor();

    // 线程池
    private final ExecutorService globalExecutor;
    private final ExecutorService lowPriorityExecutor;

    // 命名空间控制器
    private final ConcurrentMap<String, NamespaceController> namespaceControllers = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, ConcurrentGroup> concurrentGroups = new ConcurrentHashMap<>();

    // 监控接口
    private Monitor monitor = new LogMonitor();

    private GradientExecutor() {
        // 创建全局线程池
        globalExecutor = new ThreadPoolExecutor(
                0, GLOBAL_MAX_THREADS,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new NamedThreadFactory("global-pool")
        );

        // 创建低优先级线程池
        lowPriorityExecutor = new ThreadPoolExecutor(
                0, LOW_PRIORITY_POOL_SIZE,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new NamedThreadFactory("low-priority-pool")
        );
    }

    public static GradientExecutor getInstance() {
        return INSTANCE;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public void execute(String namespace, Runnable task) {
        execute(namespace, task, Opt.defaults());
    }

    public void execute(String namespace, Runnable task, Opt options) {
        // 获取或创建命名空间控制器
        NamespaceController controller = namespaceControllers.computeIfAbsent(
                namespace, k -> new NamespaceController(options.getMaxConcurrent())
        );

        // 包装任务
        TrackedRunnable trackedTask = new TrackedRunnable(namespace, task, options, controller);

        try {
            // 尝试执行任务
            if (!tryExecute(namespace, trackedTask, options, controller)) {
                handleRejection(namespace, trackedTask, options);
            }
        } catch (Exception e) {
            monitor.reportRejection(namespace, e);
        }
    }

    private boolean tryExecute(String namespace, TrackedRunnable task, Opt options, NamespaceController controller) {
        // 尝试获取执行许可
        if (!controller.tryAcquire()) {
            return false;
        }

        // 提交到合适的线程池
        try {
            if (options.isLowPriority()) {
                lowPriorityExecutor.execute(task);
            } else {
                globalExecutor.execute(task);
            }
            return true;
        } catch (RejectedExecutionException e) {
            controller.release();
            throw e;
        }
    }

    private void handleRejection(String namespace, Runnable task, Opt options) {
        switch (options.getRejectPolicy()) {
            case CALLER_RUNS:
                executeCallerRuns(namespace, task);
                break;
            case ABORT:
                monitor.reportRejection(namespace, new RejectedExecutionException(
                        "Abort policy rejected task in namespace: " + namespace));
                break;
            case DISCARD:
                // 静默丢弃
                break;
            default:
                throw new IllegalStateException("Unknown rejection policy");
        }
    }

    private void executeCallerRuns(String namespace, Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            monitor.reportError(namespace, e);
        }
    }

    public void waitForGroup(String groupId) throws InterruptedException {
        ConcurrentGroup group = concurrentGroups.get(groupId);
        if (group != null) {
            group.await();
        }
    }

    // 内部类：命名空间控制器
    private static class NamespaceController {
        private final Lock lock = new ReentrantLock();
        private final Condition condition = lock.newCondition();
        private final AtomicInteger activeCount = new AtomicInteger(0);
        private volatile int maxConcurrent;

        public NamespaceController(int maxConcurrent) {
            this.maxConcurrent = Math.max(1, maxConcurrent);
        }

        public void setMaxConcurrent(int maxConcurrent) {
            lock.lock();
            try {
                this.maxConcurrent = Math.max(1, maxConcurrent);
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        public boolean tryAcquire() {
            if (maxConcurrent <= 0) return true; // 无限制

            lock.lock();
            try {
                // 快速尝试
                if (activeCount.get() < maxConcurrent) {
                    activeCount.incrementAndGet();
                    return true;
                }

                return false;
            } finally {
                lock.unlock();
            }
        }

        public void acquire() throws InterruptedException {
            if (maxConcurrent <= 0) return; // 无限制

            lock.lock();
            try {
                while (activeCount.get() >= maxConcurrent) {
                    condition.await();
                }
                activeCount.incrementAndGet();
            } finally {
                lock.unlock();
            }
        }

        public void release() {
            if (maxConcurrent <= 0) return; // 无限制

            lock.lock();
            try {
                activeCount.decrementAndGet();
                condition.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    // 内部类：任务跟踪
    private class TrackedRunnable implements Runnable {
        private final String namespace;
        private final Runnable task;
        private final Opt options;
        private final NamespaceController controller;
        private final long startTime;

        public TrackedRunnable(String namespace, Runnable task, Opt options, NamespaceController controller) {
            this.namespace = namespace;
            this.task = task;
            this.options = options;
            this.controller = controller;
            this.startTime = System.nanoTime();

            // 注册到任务组
            if (options.getGroupId() != null) {
                concurrentGroups.computeIfAbsent(
                        options.getGroupId(),
                        k -> new ConcurrentGroup()
                ).addTask();
            }
        }

        @Override
        public void run() {
            try {
                // 执行前监控
                monitor.beforeExecute(namespace);

                // 执行任务
                task.run();

                // 执行后监控
                monitor.afterExecute(namespace, System.nanoTime() - startTime);
            } catch (Throwable t) {
                // 异常处理
                monitor.reportError(namespace, t);
            } finally {
                // 释放资源
                controller.release();

                // 通知任务组完成
                if (options.getGroupId() != null) {
                    ConcurrentGroup group = concurrentGroups.get(options.getGroupId());
                    if (group != null) {
                        group.taskDone();
                    }
                }
            }
        }
    }

    // 内部类：并发任务组
    private static class ConcurrentGroup {
        private final Lock lock = new ReentrantLock();
        private final Condition condition = lock.newCondition();
        private int activeTasks = 0;

        public void addTask() {
            lock.lock();
            try {
                activeTasks++;
            } finally {
                lock.unlock();
            }
        }

        public void taskDone() {
            lock.lock();
            try {
                if (--activeTasks == 0) {
                    condition.signalAll();
                }
            } finally {
                lock.unlock();
            }
        }

        public void await() throws InterruptedException {
            lock.lock();
            try {
                while (activeTasks > 0) {
                    condition.await();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    // 配置选项类
    public static class Opt {
        private int maxConcurrent = 0; // 0表示无限制
        private RejectPolicy rejectPolicy = RejectPolicy.CALLER_RUNS;
        private boolean lowPriority = false;
        private String groupId;

        public static Opt defaults() {
            return new Opt();
        }

        public static Opt withMaxConcurrent(int max) {
            Opt opt = new Opt();
            opt.maxConcurrent = max;
            return opt;
        }

        public static Opt withRejectPolicy(RejectPolicy policy) {
            Opt opt = new Opt();
            opt.rejectPolicy = policy;
            return opt;
        }

        public static Opt withLowPriority() {
            Opt opt = new Opt();
            opt.lowPriority = true;
            return opt;
        }

        public static Opt withConcurrentGroup(String groupId) {
            Opt opt = new Opt();
            opt.groupId = groupId;
            return opt;
        }

        // Getters
        public int getMaxConcurrent() {
            return maxConcurrent;
        }

        public RejectPolicy getRejectPolicy() {
            return rejectPolicy;
        }

        public boolean isLowPriority() {
            return lowPriority;
        }

        public String getGroupId() {
            return groupId;
        }
    }

    // 拒绝策略枚举
    public enum RejectPolicy {
        CALLER_RUNS,   // 在调用线程执行
        ABORT,         // 抛出异常
        DISCARD        // 静默丢弃
    }

    // 监控接口
    public interface Monitor {
        void beforeExecute(String namespace);

        void afterExecute(String namespace, long nanos);

        void reportError(String namespace, Throwable t);

        void reportRejection(String namespace, Throwable t);
    }

    // 默认监控实现
    private static class LogMonitor implements Monitor {
        @Override
        public void beforeExecute(String namespace) {
            // 实际实现中可记录日志或上报监控
        }

        @Override
        public void afterExecute(String namespace, long nanos) {
            // 实际实现中可记录执行时间
        }

        @Override
        public void reportError(String namespace, Throwable t) {
            System.err.println("Error in namespace [" + namespace + "]: " + t.getMessage());
            t.printStackTrace();
        }

        @Override
        public void reportRejection(String namespace, Throwable t) {
            System.err.println("Rejection in namespace [" + namespace + "]: " + t.getMessage());
        }
    }

    // 命名线程工厂
    private static class NamedThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final AtomicInteger counter = new AtomicInteger(1);

        public NamedThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, namePrefix + "-" + counter.getAndIncrement());
        }
    }
}