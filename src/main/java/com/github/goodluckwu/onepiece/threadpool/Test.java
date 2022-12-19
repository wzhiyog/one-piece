package com.github.goodluckwu.onepiece.threadpool;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import com.sun.jdmk.comm.HtmlAdaptorServer;

public class Test {
    private static Random random = new Random();
    public static void main(String[] args) throws MalformedObjectNameException, InterruptedException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        ThreadPoolMonitor es1 = ThreadPoolMonitor.newCachedThreadPool("test-pool-1");
        ThreadPoolParam threadPoolParam1 = new ThreadPoolParam(es1);

        ThreadPoolMonitor es2 = ThreadPoolMonitor.newCachedThreadPool("test-pool-2");
        ThreadPoolParam threadPoolParam2 = new ThreadPoolParam(es2);

        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        server.registerMBean(threadPoolParam1, new ObjectName("test-pool-1:type=threadPoolParam"));
        server.registerMBean(threadPoolParam2, new ObjectName("test-pool-2:type=threadPoolParam"));

        //http连接的方式查看监控任务
        HtmlAdaptorServer adapter = new HtmlAdaptorServer();
        server.registerMBean(adapter, new ObjectName(String.format("HtmlAgent:name=html-adapter, port=%s", 8082)));
        adapter.start();

        executeTask(es1);
        executeTask(es2);
        Thread.sleep(1000 * 60 * 60);
    }

    private static void executeTask(ExecutorService es) {
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                int temp = i;
                es.submit(() -> {
                    //随机睡眠时间
                    try {
                        Thread.sleep(random.nextInt(60) * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(temp);
                });
            }
        }).start();
    }
}
