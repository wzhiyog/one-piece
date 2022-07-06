package com.github.goodluckwu.onepiece.executor;

import com.github.goodluckwu.onepiece.context.Context;

import java.util.concurrent.*;

public class ContextThreadPoolExecutor extends ThreadPoolExecutor {

    public ContextThreadPoolExecutor(int corePoolSize,
                                     int maximumPoolSize,
                                     long keepAliveTime,
                                     TimeUnit unit,
                                     BlockingQueue<Runnable> workQueue,
                                     ThreadFactory threadFactory,
                                     RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        Context.clear();
    }
}