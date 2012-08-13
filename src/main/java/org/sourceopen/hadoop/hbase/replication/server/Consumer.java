/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.sourceopen.hadoop.hbase.replication.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.sourceopen.hadoop.hbase.replication.consumer.ConsumerConstants;
import org.sourceopen.hadoop.hbase.replication.consumer.FileChannelManager;
import org.sourceopen.hadoop.hbase.replication.producer.ProducerConstants;

/**
 * 类Consumer.java的实现描述：Consumer的main线程
 * 
 * @author dongsh 2012-3-4 下午02:17:18
 */
public class Consumer {

    private static final Logger LOG         = LoggerFactory.getLogger(Consumer.class);
    private static final String SPRING_PATH = "classpath*:META-INF/spring/context.xml";
    private static boolean      running     = true;

    public static void main(String args[]) {
        try {
            final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(SPRING_PATH);
            // 钩子
            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run() {
                    try {
                        context.stop();
                        context.close();
                        LOG.info("Consumer server stopped");
                        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                                           + " Consumer server stoped");
                    } catch (Throwable t) {
                        LOG.error("Fail to stop consumer server: ", t);
                    }
                    synchronized (Consumer.class) {
                        running = false;
                        Consumer.class.notify();
                    }

                }
            });
            Configuration conf = HBaseConfiguration.create();
            conf.addResource(ConsumerConstants.COMMON_CONFIG_FILE);
            conf.addResource(ConsumerConstants.CONSUMER_CONFIG_FILE);
            Consumer.start(conf);
            LOG.info("Consumer server started");
            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                               + " Consumer server started");

        } catch (Throwable t) {
            LOG.error("Fail to start consumer server: ", t);
            System.exit(-1);
        }
        synchronized (Consumer.class) {
            while (running) {
                try {
                    Consumer.class.wait();
                } catch (Throwable t) {
                    LOG.error("Consumer server got runtime errors: ", t);
                }
            }
        }
    }

    private static void start(Configuration conf) {
        
    }

}
