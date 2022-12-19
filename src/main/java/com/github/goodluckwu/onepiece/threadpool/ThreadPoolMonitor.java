package com.github.goodluckwu.onepiece.threadpool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadPoolMonitor extends ThreadPoolExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolMonitor.class);

    /**
     * ActiveCount
     * */
    int ac = 0;

    /**
     * 当前所有线程消耗的时间
     * */
    private final AtomicLong totalCostTime = new AtomicLong();

    /**
     * 当前执行的线程总数
     * */
    private final AtomicLong totalTasks = new AtomicLong();

    /**
     * 线程池名称
     */
    private final String poolName;

    /**
     * 最短 执行时间
     * */
    private long minCostTime;

    /**
     * 最长执行时间
     * */
    private long maxCostTime;


    /**
     * 保存任务开始执行的时间
     */
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    public ThreadPoolMonitor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                             TimeUnit unit, BlockingQueue<Runnable> workQueue, String poolName) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                Executors.defaultThreadFactory(), poolName);
    }

    public ThreadPoolMonitor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                             TimeUnit unit, BlockingQueue<Runnable> workQueue,
                             ThreadFactory threadFactory, String poolName) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        this.poolName = poolName;
    }

    public static ThreadPoolMonitor newFixedThreadPool(int nThreads, String poolName) {
        return new ThreadPoolMonitor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), poolName);
    }

    public static ThreadPoolMonitor newCachedThreadPool(String poolName) {
        return new ThreadPoolMonitor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), poolName);
    }

    public static ThreadPoolMonitor newSingleThreadExecutor(String poolName) {
        return new ThreadPoolMonitor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), poolName);
    }

    /**
     * 线程池延迟关闭时（等待线程池里的任务都执行完毕），统计线程池情况
     */
    @Override
    public void shutdown() {
        // 统计已执行任务、正在执行任务、未执行任务数量
        logger.info("{} Going to shutdown. Executed tasks: {}, Running tasks: {}, Pending tasks: {}",
                this.poolName, this.getCompletedTaskCount(), this.getActiveCount(), this.getQueue().size());
        super.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        // 统计已执行任务、正在执行任务、未执行任务数量
        logger.info("{} Going to immediately shutdown. Executed tasks: {}, Running tasks: {}, Pending tasks: {}",
                this.poolName, this.getCompletedTaskCount(), this.getActiveCount(), this.getQueue().size());
        return super.shutdownNow();
    }

    /**
     * 任务执行之前，记录任务开始时间
     */
    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        startTime.set(System.currentTimeMillis());
    }

    /**
     * 任务执行之后，计算任务结束时间
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        long costTime = System.currentTimeMillis() - startTime.get();
        startTime.remove();  //删除，避免占用太多内存
        //设置最大最小执行时间
        maxCostTime = maxCostTime > costTime ? maxCostTime : costTime;
        if (totalTasks.get() == 0) {
            minCostTime = costTime;
        }
        minCostTime = minCostTime < costTime ? minCostTime : costTime;
        totalCostTime.addAndGet(costTime);
        totalTasks.incrementAndGet();

        logger.info("{}-pool-monitor: " +
                        "Duration: {} ms, PoolSize: {}, CorePoolSize: {}, ActiveCount: {}, " +
                        "Completed: {}, Task: {}, Queue: {}, LargestPoolSize: {}, " +
                        "MaximumPoolSize: {},  KeepAliveTime: {}, isShutdown: {}, isTerminated: {}",
                this.poolName,
                costTime, this.getPoolSize(), this.getCorePoolSize(), super.getActiveCount(),
                this.getCompletedTaskCount(), this.getTaskCount(), this.getQueue().size(), this.getLargestPoolSize(),
                this.getMaximumPoolSize(), this.getKeepAliveTime(TimeUnit.MILLISECONDS), this.isShutdown(), this.isTerminated());
    }

    public int getAc() {
        return ac;
    }

    /**
     * 线程平均耗时
     *
     * @return
     * */
    public float getAverageCostTime() {
        return totalCostTime.get() / totalTasks.get();
    }

    /**
     * 线程最大耗时
     * */
    public long getMaxCostTime() {
        return maxCostTime;
    }

    /**
     * 线程最小耗时
     * */
    public long getMinCostTime() {
        return minCostTime;
    }

}
