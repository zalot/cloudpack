/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.consumer;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 类Server.java的实现描述：初始线程 
 * @author dongsh 2012-2-28 上午10:34:14
 */
public class Server {

    private static final Logger LOG         = LoggerFactory.getLogger(Server.class);
    private static final String CONFIG_PATH = "classpath*:META-INF/spring/replication-*.xml";

    public static void main(String args[]) {
        try {
            // 启动Server
            final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_PATH);
            context.start();
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
                }
            });

            LOG.info("Consumer server started");
            System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                               + " Consumer server started");

            synchronized (Server.class) {
                while (true) {
                    try {
                        Server.class.wait();
                    } catch (Throwable t) {
                        LOG.error("Consumer server got runtime errors: ", t);
                    }
                }
            }
        } catch (Throwable t) {
            System.exit(-1);
            LOG.error("Fail to start consumer server: ", t);
        }
    }

}
