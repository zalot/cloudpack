package org.sourceopen.hadoop.hbase.replication.producer;

import org.sourceopen.hadoop.hbase.replication.protocol.HDFSFileAdapter;

/**
 * 类ProducerConstants.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Mar 16, 2012 11:07:56 AM
 */
public class ProducerConstants {

    public static final String CONF_PREFIX                            = "";

    public static final String PRODUCER_CONFIG_FILE                   = "META-INF/producer-configuration.xml";
    public static final String COMMON_CONFIG_FILE                     = "META-INF/common-configuration.xml";

    // -----------------------------------------
    // Base
    // -----------------------------------------
    public static final String CONFKEY_ROOT_HBASE_HDFS                = CONF_PREFIX
                                                                        + "hbase.rep.producer.hbase.rootdir";
    public static String       CONFKEY_ROOT_ZOO                       = CONF_PREFIX
                                                                        + "hbase.replication.producer.zoo.root";
    public static final String ROOT_HBASE_HDFS                        = "/hbase";
    public static String       ROOT_ZOO                               = "hbaserep";

    // -----------------------------------------
    // Base Zoo Path
    // -----------------------------------------
    public static String       CONFKEY_ZOO_LOCK_ROOT                  = CONF_PREFIX
                                                                        + "hbase.rep.producer.zoo.lock.root";
    // public static String ZOO_LOCK_ROOT = "/alireplock";

    // -----------------------------------------
    // Scan Thread
    // -----------------------------------------
    public static String       CONFKEY_ZOO_SCAN_LOCK_FLUSHSLEEPTIME   = CONF_PREFIX
                                                                        + "hbase.rep.producer.zoo.lock.scan.sleeptime";
    public static long         ZOO_SCAN_LOCK_FLUSHSLEEPTIME           = 20000;

    public static String       CONFKEY_ZOO_SCAN_LOCK_RETRYTIME        = CONF_PREFIX
                                                                        + "hbase.rep.producer.zoo.lock.scan.retrytime";
    public static long         ZOO_SCAN_LOCK_RETRYTIME                = 10000;

    // -----------------------------------------
    // Replication Thread
    // -----------------------------------------
    public static String       CONFKEY_HLOG_READERBUFFER              = CONF_PREFIX
                                                                        + "hbase.rep.producer.hlog.readerbuffer";
    public static long         HLOG_READERBUFFER                      = 50000;

    public static String       CONFKEY_HLOG_GROUP_INTERVAL            = CONF_PREFIX
                                                                        + "hbase.rep.producer.hlog.group.interval";
    public static long         HLOG_GROUP_INTERVAL                    = 3000;

    public static final String CONFKEY_LOGREADER_CLASS                = CONF_PREFIX
                                                                        + "hbase.rep.producer.hlog.logreader.class";
    public static final String CONFKEY_CROSSIDC_REPLICATION_SLEEPTIME = CONF_PREFIX
                                                                        + "hbase.rep.producer.hlog.sleeptime";
    public static final int    CROSSIDC_REPLICATION_SLEEPTIME         = 2000;

    // -----------------------------------------
    // Reject Thread
    // -----------------------------------------
    public static String       CONFKEY_ZOO_REJECT_LOCK_FLUSHSLEEPTIME = CONF_PREFIX
                                                                        + "hbase.rep.producer.zoo.lock.reject.sleeptime";
    public static long         ZOO_REJECT_LOCK_FLUSHSLEEPTIME         = 20000;

    public static String       CONFKEY_ZOO_REJECT_LOCK_RETRYTIME      = CONF_PREFIX
                                                                        + "hbase.rep.producer.zoo.lock.reject.retrytime";
    public static long         ZOO_REJECT_LOCK_RETRYTIME              = 10000;

    // -----------------------------------------
    // Start Manager
    // -----------------------------------------
    public static final String CONFKEY_REP_SCANNER_POOL_SIZE          = CONF_PREFIX
                                                                        + "hbase.rep.producer.scannerPoolSize";
    public static final int    REP_SCANNER_POOL_SIZE                  = 1;
    public static final String CONFKEY_THREADPOOL_SIZE                = CONF_PREFIX
                                                                        + "hbase.rep.producer.threadpool.queuesize";
    public static final String CONFKEY_THREADPOOL_KEEPALIVE_TIME      = CONF_PREFIX
                                                                        + "hbase.rep.producer.threadpool.keepAliveTime";
    public static final String CONFKEY_REP_SINK_POOL_SIZE             = CONF_PREFIX
                                                                        + "hbase.rep.producer.hlog.replicationPoolSize";
    public static final int    REP_SINK_POOL_SIZE                     = 10;
    public static final String CONFKEY_REP_REJECT_POOL_SIZE           = CONF_PREFIX
                                                                        + "hbase.rep.producer.scannerPoolSize";
    public static final int    REP_REJECT_POOL_SIZE                   = 1;

    // -----------------------------------------
    // protocol
    // -----------------------------------------
    public static final String CONFKEY_PROTOCOL_VERSION               = CONF_PREFIX + "hbase.rep.protocol.version";
    public static final int    PROTOCOL_VERSION                       = 1;

    public static final String CONFKEY_PROTOCOL_CLASS                 = CONF_PREFIX + "hbase.rep.protocol.class";
    public static final String PROTOCOL_CLASS                         = HDFSFileAdapter.class.getCanonicalName();

    // -----------------------------------------
    // static field
    // -----------------------------------------
    // public static String ZOO_LOCK_SCAN = "/lockscan";
    // public static String ZOO_LOCK_REJECT_SCAN = "/lockrejectscan";
    // public static String ZOO_LOCK_CRUSH_SCAN = "/lockcrushscan";
    public static String       ZOO_PERSISTENCE_HLOG_GROUP             = "hlogroups";
    public static String       ZOO_PERSISTENCE_HLOG_GROUP_LOCK        = "glock";

    public static String       PATH_BASE_HLOG                         = ".logs";
    public static String       PATH_BASE_OLDHLOG                      = ".oldlogs";
}
