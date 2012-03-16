package com.alibaba.hbase.replication.utility;

public class ProducerConstants {

    // -----------------------------------------
    // Base
    // -----------------------------------------
    public static final String CONFKEY_HDFS_HBASE_ROOT                = "com.alibaba.hbase.replication.producer.hbase.rootdir";
    public static final String HDFS_HBASE_ROOT                        = "/hbase";
    public static final String PRODUCER_CONFIG_FILE                   = "META-INF/producer-configuration.xml";
    public static String       CONFKEY_ZOO_ROOT                       = "com.alibaba.hbase.replication.producer.zoo.root";
    public static String       ZOO_ROOT                               = "/alirep";

    // -----------------------------------------
    // Base Zoo Path
    // -----------------------------------------
    public static String       CONFKEY_ZOO_LOCK_ROOT                  = "com.alibaba.hbase.replication.producer.zoo.lock.root";
    public static String       ZOO_LOCK_ROOT                          = "/alireplock";

    // -----------------------------------------
    // Scan Thread
    // -----------------------------------------
    public static String       CONFKEY_ZOO_SCAN_LOCK_FLUSHSLEEPTIME   = "com.alibaba.hbase.replication.producer.zoo.lock.scan.sleeptime";
    public static long         ZOO_SCAN_LOCK_FLUSHSLEEPTIME           = 20000;

    public static String       CONFKEY_ZOO_SCAN_LOCK_RETRYTIME        = "com.alibaba.hbase.replication.producer.zoo.lock.scan.retrytime";
    public static long         ZOO_SCAN_LOCK_RETRYTIME                = 10000;

    // -----------------------------------------
    // Replication Thread
    // -----------------------------------------
    public static String       CONFKEY_HLOG_READERBUFFER              = "com.alibaba.hbase.replication.producer.hlog.readerbuffer";
    public static long         HLOG_READERBUFFER                      = 50000;

    public static String       CONFKEY_HLOG_GROUP_INTERVAL            = "com.alibaba.hbase.replication.producer.hlog.group.interval";
    public static long         HLOG_GROUP_INTERVAL                    = 5000;

    public static final String CONFKEY_LOGREADER_CLASS                = "com.alibaba.hbase.replication.producer.hlog.logreader.class";
    public static final String CONFKEY_CROSSIDC_REPLICATION_SLEEPTIME = "com.alibaba.hbase.replication.producer.hlog.sleeptime";

    // -----------------------------------------
    // Reject Thread
    // -----------------------------------------
    public static String       CONFKEY_ZOO_REJECT_LOCK_FLUSHSLEEPTIME = "com.alibaba.hbase.replication.producer.zoo.lock.reject.sleeptime";
    public static long         ZOO_REJECT_LOCK_FLUSHSLEEPTIME         = 20000;

    public static String       CONFKEY_ZOO_REJECT_LOCK_RETRYTIME      = "com.alibaba.hbase.replication.producer.zoo.lock.reject.retrytime";
    public static long         ZOO_REJECT_LOCK_RETRYTIME              = 10000;

    // -----------------------------------------
    // Start Manager
    // -----------------------------------------
    public static final String CONFKEY_REP_SCANNER_POOL_SIZE          = "com.alibaba.hbase.replication.producer.scannerPoolSize";
    public static final int    REP_SCANNER_POOL_SIZE                  = 1;
    public static final String CONFKEY_THREADPOOL_SIZE                = "com.alibaba.hbase.replication.producer.threadpool.queuesize";
    public static final String CONFKEY_THREADPOOL_KEEPALIVE_TIME      = "com.alibaba.hbase.replication.producer.threadpool.keepAliveTime";
    public static final int    CROSSIDC_REPLICATION_SLEEPTIME         = 100;
    public static final String CONFKEY_REP_SINK_POOL_SIZE             = "com.alibaba.hbase.replication.producer.hlog.replicationPoolSize";
    public static final int    REP_SINK_POOL_SIZE                     = 10;
    public static final String CONFKEY_REP_REJECT_POOL_SIZE          = "com.alibaba.hbase.replication.producer.scannerPoolSize";
    public static final int    REP_REJECT_POOL_SIZE                  = 1;
    // -----------------------------------------
    // static field
    // -----------------------------------------
    public static String       ZOO_LOCK_SCAN                          = "/lockscan";
    public static String       ZOO_LOCK_REJECT_SCAN                   = "/lockrejectscan";
    public static String       ZOO_PERSISTENCE_HLOG_GROUP             = "/aligroups";
    public static String       ZOO_PERSISTENCE_HLOG_GROUP_LOCK        = "/aliglock";

    public static String       PATH_BASE_HLOG                         = ".logs";
    public static String       PATH_BASE_OLDHLOG                      = ".oldlogs";
}
