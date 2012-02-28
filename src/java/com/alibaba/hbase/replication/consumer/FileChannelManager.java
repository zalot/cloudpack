/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hbase.replication.consumer;

import java.io.IOException;
import java.net.URI;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.MapUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.hbase.replication.protocol.FileAdapter;
import com.alibaba.hbase.replication.protocol.Head;

/**
 * 类Manager.java的实现描述：持有consumer端的中间文件同步线程池 
 * @author dongsh 2012-2-28 上午11:17:17
 */
public class FileChannelManager {

    private static final Logger LOG         = LoggerFactory.getLogger(FileChannelManager.class);
    
    private AtomicBoolean stopflag=new AtomicBoolean(false);
    private ThreadPoolExecutor            fileChannelPool;
    private int                           corefileChannelPoolSize      = 10;
    private int                           maxfileChannelPoolSize       = 10;
    private int                           queueSize         = 100;
    private int                           keepAliveTime     = 100;
    // for HDFS, like "hdfs://localhost/"
    private String                           producerFSUri;
    //"user/admin/replicationFile"
    private String                           replicationFileDirUri;
    private FileSystem fs;
    
    
    public void start() throws IOException{
        if (LOG.isInfoEnabled()) {
            LOG.info("FileChannelManager is pendding to start.");
        }
        fileChannelPool = new ThreadPoolExecutor(corefileChannelPoolSize, this.maxfileChannelPoolSize , this.keepAliveTime, TimeUnit.SECONDS,
                                                 new ArrayBlockingQueue<Runnable>(this.queueSize));
        Configuration conf = new Configuration();
        fs = FileSystem.get(URI.create(replicationFileDirUri), conf);
        scanProducerFilesAndAddToZK();
        createFileChannelRunnable();
    }
    
    public void stop(){
        if (LOG.isInfoEnabled()) {
            LOG.info("FileChannelManager is pendding to stop.");
        }
        stopflag.set(true);
    }
    
    /**
     * 
     */
    private void createFileChannelRunnable() {
        // TODO Auto-generated method stub
        
    }

    /**
     * @throws IOException 
     * 
     */
    private void scanProducerFilesAndAddToZK() throws IOException {
        // s1. scanProducerFiles
        Map<String,Set<String>> fstMap=new HashMap<String, Set<String>>();
        for (FileStatus fst : fs.listStatus(new Path(replicationFileDirUri))) {
            if(!fst.isDir()){
                String fileName=fst.getPath().getName();
                Head fileHead=FileAdapter.validataFileName(fileName);
                if(fileHead==null && LOG.isErrorEnabled()){
                    LOG.error("validataFileName fail. path: "+fst.getPath());
                    continue;
                }
                String group=fileHead.getGroupName();
                Set<String> ftsSet=fstMap.get(group);
                if(ftsSet == null){
                    ftsSet=new TreeSet<String>(new Comparator<String>() {

                        @Override
                        public int compare(String o1, String o2) {
                            // 二次排序，优先fileTimestamp，然后是headTimestamp
                            Head o1Head=FileAdapter.validataFileName(o1);
                            Head o2Head=FileAdapter.validataFileName(o2);
                            if(o1Head.getFileTimestamp()>o2Head.getFileTimestamp()) return 1;
                            if(o1Head.getFileTimestamp()<o2Head.getFileTimestamp()) return -1;
                            if(o1Head.getHeadTimestamp()>o2Head.getHeadTimestamp()) return 1;
                            if(o1Head.getHeadTimestamp()<o2Head.getHeadTimestamp()) return -1;
                            if(LOG.isWarnEnabled()){
                                LOG.warn("same timestamp with "+o1+" and "+o2);
                            }
                            return 0;
                        }
                    });
                    fstMap.put(group, ftsSet);
                }
                ftsSet.add(fileName);
            }else if(LOG.isWarnEnabled()){
                LOG.warn("Dir occurs in "+replicationFileDirUri+" .path: "+fst.getPath());
            }
        }
        // s2. update ZK
        if(MapUtils.isNotEmpty(fstMap)){
            
        }
    }

    
    
}
