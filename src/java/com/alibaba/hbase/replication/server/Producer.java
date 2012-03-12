/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.hbase.replication.producer.ReplicationSinkManger;
import com.alibaba.hbase.replication.utility.ProducerConstants;

/**
 * 类Producer.java的实现描述：Producer的main线程
 * 
 * @author dongsh 2012-3-9 上午10:42:41
 */
public class Producer {

    private static final Logger LOG         = LoggerFactory.getLogger(Producer.class);
    private static final String SPRING_PATH = "classpath*:META-INF/spring/context.xml";
    private static boolean      running     = true;

    public static void main(String args[]) {
        try {
            ReplicationConf.setFilePath(ProducerConstants.PRODUCER_CONFIG_FILE);
            final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(SPRING_PATH);
            // 钩子
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    try {
                        context.stop();
                        context.close();
                        LOG.info("Producer server stopped");
                        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                                           + " Producer server stoped");
                    } catch (Throwable t) {
                        LOG.error("Fail to stop Producer server: ", t);
                    }
                    synchronized (Producer.class) {
                        running = false;
                        Producer.class.notify();
                    }

                }
            });
            // 启动Server
            context.start();
            ((ReplicationSinkManger) context.getBean("replicationSinkManger")).start();
            LOG.info("Producer server started");
            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                               + " Producer server started");

        } catch (Throwable t) {
            System.exit(-1);
            LOG.error("Fail to start producer server: ", t);
        }
        synchronized (Producer.class) {
            while (running) {
                try {
                    Producer.class.wait();
                } catch (Throwable t) {
                    LOG.error("Producer server got runtime errors: ", t);
                }
            }
        }
    }
}
