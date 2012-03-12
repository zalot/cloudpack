/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.server;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.stereotype.Service;

/**
 * 类ReplicationConf.java的实现描述：Configuration for consumer
 * 
 * @author dongsh 2012-3-4 下午02:15:28
 */
@Service("conf")
public class ReplicationConf extends Configuration {

    private static String filePath = null;

    public static void setFilePath(String filePath) {
        ReplicationConf.filePath = filePath;
    }

    public ReplicationConf(){
        super();
        // 添加hbase的默认配置
        HBaseConfiguration.addHbaseResources(this);
        // 添加自定义的配置
        this.addResource(filePath);
    }

}
