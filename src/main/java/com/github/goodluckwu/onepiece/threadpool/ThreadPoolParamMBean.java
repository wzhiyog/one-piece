package com.github.goodluckwu.onepiece.threadpool;

public interface ThreadPoolParamMBean {
    /**
     * 线程池中正在执行任务的线程数量
     *
     * @return
     */
    int getActiveCount();

    /**
     * 线程池已完成的任务数量
     *
     * @return
     */
    long getCompletedTaskCount();

    /**
     * 线程池的核心线程数量
     *
     * @return
     */
    int getCorePoolSize();

    /**
     * 线程池曾经创建过的最大线程数量
     *
     * @return
     */
    int getLargestPoolSize();

    /**
     * 线程池的最大线程数量
     *
     * @return
     */
    int getMaximumPoolSize();

    /**
     * 线程池当前的线程数量
     *
     * @return
     */
    int getPoolSize();

    /**
     * 线程池需要执行的任务数量
     *
     * @return
     */
    long getTaskCount();

    /**
     * 线程最大耗时
     *
     * @return
     * */
    long getMaxCostTime();

    /**
     * 线程最小耗时
     *
     * @return
     * */
    long getMinCostTime();

    /**
     * 线程平均耗时
     *
     * @return
     * */
    float getAverageCostTime();
}
