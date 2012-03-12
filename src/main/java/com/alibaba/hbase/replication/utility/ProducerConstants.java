package com.alibaba.hbase.replication.utility;

public class ProducerConstants {

    public static final String PRODUCER_CONFIG_FILE                 = "META-INF/producer-configuration.xml";
    public static String       CONFKEY_ZOO_ROOT                     = "com.alibaba.hbase.replication.producer.zoo.root";
    public static String       ZOO_ROOT                             = "/alirep";

    public static String       CONFKEY_ZOO_SCAN_ROOT                = "com.alibaba.hbase.replication.producer.zoo.scan.root";
    public static String       ZOO_SCAN_ROOT                        = "/alirepscanlock";

    public static String       CONFKEY_ZOO_SCAN_OLDHLOG_TIMEOUT     = "com.alibaba.hbase.replication.producer.zoo.scan.oldhlog.timeout";
    public static long         ZOO_SCAN_OLDHLOG_TIMEOUT             = 600000;

    public static String       CONFKEY_ZOO_SCAN_LOCK_FLUSHSLEEPTIME = "com.alibaba.hbase.replication.producer.zoo.scan.sleeptime";
    public static long         ZOO_SCAN_LOCK_FLUSHSLEEPTIME         = 20000;

    public static String       CONFKEY_ZOO_SCAN_LOCK_TRYLOCKTIME    = "com.alibaba.hbase.replication.producer.zoo.scan.retrytime";
    public static long         ZOO_SCAN_LOCK_TRYLOCKTIME            = 30000;

    public static String       CONFKEY_HLOG_READERBUFFER            = "com.alibaba.hbase.replication.producer.hlog.readerbuffer";
    public static long         HLOG_READERBUFFER                    = 50000;

    public static String       CONFKEY_HLOGGROUP_INTERVAL           = "com.alibaba.hbase.replication.producer.hlog.group.interval";
    public static long         HLOG_GROUP_INTERVAL                  = 10000;

    public static final String CONFKEY_REP_SINK_POOL_SIZE           = "com.alibaba.hbase.replication.producer.replicationPoolSize";
    public static final int    REP_SINK_POOL_SIZE                   = 10;

    public static final String CONFKEY_REP_SCANNER_POOL_SIZE        = "com.alibaba.hbase.replication.producer.scannerPoolSize";
    public static final int    REP_SCANNER_POOL_SIZE                = 1;

    public static final String CONFKEY_THREADPOOL_SIZE              = "com.alibaba.hbase.replication.producer.threadpool.queuesize";
    public static final String CONFKEY_THREADPOOL_KEEPALIVE_TIME    = "com.alibaba.hbase.replication.producer.threadpool.keepAliveTime";

    public static String       ZOO_SCAN_LOCK                        = "/scanlock";
    public static String       ZOO_PERSISTENCE_HLOG_GROUP           = "/aligroups";
    public static String       ZOO_PERSISTENCE_HLOG_GROUP_LOCK      = "/aliglock";

    public static String       PATH_BASE_HLOG                       = ".logs";
    public static String       PATH_BASE_OLDHLOG                    = ".oldlogs";
}
