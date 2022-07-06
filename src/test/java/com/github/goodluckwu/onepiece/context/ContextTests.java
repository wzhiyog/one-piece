package com.github.goodluckwu.onepiece.context;

import com.github.goodluckwu.onepiece.executor.ContextThreadPoolExecutor;

import java.util.concurrent.*;

public class ContextTests {
    //模拟业务线程池
    private static final ExecutorService threadPool = new ContextThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) throws InterruptedException {
        //模拟同时10个web请求，一个请求一个线程
        for (int i = 0; i < 10; i++) {
            new TomcatThread(i).start();
        }

        Thread.sleep(3000);
        threadPool.shutdown();
    }

    static class TomcatThread extends Thread {
        //线程下标
        int index;

        public TomcatThread(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            String parentThreadName = Thread.currentThread().getName();
            //父线程中将index值塞入线程上下文变量
            System.out.println(parentThreadName + ":" + index);
            Context.put("a", index);
            threadPool.submit(() -> {
                new BusinessThread(parentThreadName).run();
            });
        }
    }

    static class BusinessThread{
        //父进程名称
        private final String parentThreadName;

        public BusinessThread(String parentThreadName) {
            this.parentThreadName = parentThreadName;
        }


        public void run() {
            System.out.println("parent:" + parentThreadName + ":" + Context.get("a"));
        }
    }
}
