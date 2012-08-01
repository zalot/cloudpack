package org.sourceopen.hadoop.hbase.replication.utility;

import org.sourceopen.hadoop.hbase.replication.protocol.HDFSFileAdapter;

/**
 * 类ProducerConstants.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 16, 2012 11:07:56 AM
 */
public class ProducerConstants {

    public static final String CONF_SUFFIX                            = "org.sourceopen.";

    // -----------------------------------------
    // Base
    // -----------------------------------------
    public static final String CONFKEY_HDFS_HBASE_ROOT                = CONF_SUFFIX
                                                                        + "hbase.replication.producer.hbase.rootdir";
    public static final String HDFS_HBASE_ROOT                        = "/hbase";
    public static final String PRODUCER_CONFIG_FILE                   = "META-INF/producer-configuration.xml";
    public static final String COMMON_CONFIG_FILE                     = "META-INF/common-configuration.xml";
    public static String       CONFKEY_ZOO_ROOT                       = CONF_SUFFIX
                                                                        + "hbase.replication.producer.zoo.root";
    public static String       ZOO_ROOT                               = "/alirep";

    // -----------------------------------------
    // Base Zoo Path
    // -----------------------------------------
    public static String       CONFKEY_ZOO_LOCK_ROOT                  = "org.sourceopen.hadoop.hbase.replication.producer.zoo.lock.root";
    public static String       ZOO_LOCK_ROOT                          = "/alireplock";

    // -----------------------------------------
    // Scan Thread
    // -----------------------------------------
    public static String       CONFKEY_ZOO_SCAN_LOCK_FLUSHSLEEPTIME   = "org.sourceopen.hadoop.hbase.replication.producer.zoo.lock.scan.sleeptime";
    public static long         ZOO_SCAN_LOCK_FLUSHSLEEPTIME           = 20000;

    public static String       CONFKEY_ZOO_SCAN_LOCK_RETRYTIME        = "org.sourceopen.hadoop.hbase.replication.producer.zoo.lock.scan.retrytime";
    public static long         ZOO_SCAN_LOCK_RETRYTIME                = 10000;

    // -----------------------------------------
    // Replication Thread
    // -----------------------------------------
    public static String       CONFKEY_HLOG_READERBUFFER              = "org.sourceopen.hadoop.hbase.replication.producer.hlog.readerbuffer";
    public static long         HLOG_READERBUFFER                      = 50000;

    public static String       CONFKEY_HLOG_GROUP_INTERVAL            = "org.sourceopen.hadoop.hbase.replication.producer.hlog.group.interval";
    public static long         HLOG_GROUP_INTERVAL                    = 3000;

    public static final String CONFKEY_LOGREADER_CLASS                = "org.sourceopen.hadoop.hbase.replication.producer.hlog.logreader.class";
    public static final String CONFKEY_CROSSIDC_REPLICATION_SLEEPTIME = "org.sourceopen.hadoop.hbase.replication.producer.hlog.sleeptime";
    public static final int    CROSSIDC_REPLICATION_SLEEPTIME         = 2000;

    // -----------------------------------------
    // Reject Thread
    // -----------------------------------------
    public static String       CONFKEY_ZOO_REJECT_LOCK_FLUSHSLEEPTIME = "org.sourceopen.hadoop.hbase.replication.producer.zoo.lock.reject.sleeptime";
    public static long         ZOO_REJECT_LOCK_FLUSHSLEEPTIME         = 20000;

    public static String       CONFKEY_ZOO_REJECT_LOCK_RETRYTIME      = "org.sourceopen.hadoop.hbase.replication.producer.zoo.lock.reject.retrytime";
    public static long         ZOO_REJECT_LOCK_RETRYTIME              = 10000;

    // -----------------------------------------
    // Start Manager
    // -----------------------------------------
    public static final String CONFKEY_REP_SCANNER_POOL_SIZE          = "org.sourceopen.hadoop.hbase.replication.producer.scannerPoolSize";
    public static final int    REP_SCANNER_POOL_SIZE                  = 1;
    public static final String CONFKEY_THREADPOOL_SIZE                = "org.sourceopen.hadoop.hbase.replication.producer.threadpool.queuesize";
    public static final String CONFKEY_THREADPOOL_KEEPALIVE_TIME      = "org.sourceopen.hadoop.hbase.replication.producer.threadpool.keepAliveTime";
    public static final String CONFKEY_REP_SINK_POOL_SIZE             = "org.sourceopen.hadoop.hbase.replication.producer.hlog.replicationPoolSize";
    public static final int    REP_SINK_POOL_SIZE                     = 10;
    public static final String CONFKEY_REP_REJECT_POOL_SIZE           = "org.sourceopen.hadoop.hbase.replication.producer.scannerPoolSize";
    public static final int    REP_REJECT_POOL_SIZE                   = 1;

    // -----------------------------------------
    // protocol
    // -----------------------------------------
    public static final String CONFKEY_PROTOCOL_VERSION               = "org.sourceopen.hadoop.hbase.replication.protocol.version";
    public static final int    PROTOCOL_VERSION                       = 1;

    public static final String CONFKEY_PROTOCOL_CLASS                 = "org.sourceopen.hadoop.hbase.replication.protocol.class";
    public static final String PROTOCOL_CLASS                         = HDFSFileAdapter.class.getCanonicalName();

    // -----------------------------------------
    // static field
    // -----------------------------------------
    public static String       ZOO_LOCK_SCAN                          = "/lockscan";
    public static String       ZOO_LOCK_REJECT_SCAN                   = "/lockrejectscan";
    public static String       ZOO_LOCK_CRUSH_SCAN                    = "/lockcrushscan";
    public static String       ZOO_PERSISTENCE_HLOG_GROUP             = "/aligroups";
    public static String       ZOO_PERSISTENCE_HLOG_GROUP_LOCK        = "/aliglock";

    public static String       PATH_BASE_HLOG                         = ".logs";
    public static String       PATH_BASE_OLDHLOG                      = ".oldlogs";
}
