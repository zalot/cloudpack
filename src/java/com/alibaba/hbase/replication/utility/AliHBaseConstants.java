package com.alibaba.hbase.replication.utility;

public class AliHBaseConstants {
    
    public static String CONFKEY_ZOO_ROOT                     = "com.alibaba.hbase.replication.zoo.root";
    public static String ZOO_ROOT                             = "/alirep";

    public static String CONFKEY_ZOO_SCAN_ROOT                = "com.alibaba.hbase.replication.zoo.scan.root";
    public static String ZOO_SCAN_ROOT                        = "/alirepscanlock";
    
    public static String CONFKEY_ZOO_SCAN_OLDHLOG_TIMEOUT     = "com.alibaba.hbase.replication.zoo.scan.oldhlog.timeout";
    public static long   ZOO_SCAN_OLDHLOG_TIMEOUT             = 600000;
    
    public static String CONFKEY_ZOO_SCAN_LOCK_FLUSHSLEEPTIME = "com.alibaba.hbase.replication.zoo.scan.sleeptime";
    public static long   ZOO_SCAN_LOCK_FLUSHSLEEPTIME         = 20000;
    
    public static String CONFKEY_ZOO_SCAN_LOCK_TRYLOCKTIME    = "com.alibaba.hbase.replication.zoo.scan.retrytime";
    public static long   ZOO_SCAN_LOCK_TRYLOCKTIME            = 30000;
    
    
    public static String ZOO_SCAN_LOCK                        = "/scanlock";
    public static String ZOO_PERSISTENCE_HLOG_GROUP           = "/aligroups";
    public static String ZOO_PERSISTENCE_HLOG_GROUP_LOCK      = "/aliglock";
    
    public static String PATH_BASE_HLOG                       = ".logs";
    public static String PATH_BASE_OLDHLOG                    = ".oldlogs";
}
