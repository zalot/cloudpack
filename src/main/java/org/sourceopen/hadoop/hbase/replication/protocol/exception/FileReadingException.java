/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package org.sourceopen.hadoop.hbase.replication.protocol.exception;


/**
 * 类FileReadingException.java的实现描述：文件读取出错 
 * @author dongsh 2012-3-2 上午11:56:59
 */
public class FileReadingException extends Exception {

    private static final long serialVersionUID = -5519526125660776016L;

    public FileReadingException(String string, Exception e){
        super(string, e);
    }
}
