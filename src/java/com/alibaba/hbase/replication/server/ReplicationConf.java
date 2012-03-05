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

import com.alibaba.hbase.replication.consumer.Constants;

/**
 * 类ReplicationConf.java的实现描述：Configuration for consumer
 * 
 * @author dongsh 2012-3-4 下午02:15:28
 */
@Service("consumerConf")
public class ReplicationConf extends Configuration {

    public ReplicationConf(){
        super();
        //添加hbase的默认配置
        HBaseConfiguration.addHbaseResources(this);
        //添加自定义的配置
        this.addResource(Constants.CONFIG_FILE);
    }

}
