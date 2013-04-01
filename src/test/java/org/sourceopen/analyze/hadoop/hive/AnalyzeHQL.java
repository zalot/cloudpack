package org.sourceopen.analyze.hadoop.hive;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.http.HttpServer;
import org.sourceopen.analyze.hadoop.hive.datagenerator.HiveTableCreate_User_Role_Sell;
import org.sourceopen.base.HiveBase;
import org.sourceopen.hadoop.hive.ql.http.QueryPlanHelper;

public class AnalyzeHQL extends HiveBase {

    public static void main(String[] s) throws Exception {
        startMemoryHive("mem", new Configuration());
        run(HiveTableCreate_User_Role_Sell.getCreateDataBaseHQL());
        run(HiveTableCreate_User_Role_Sell.getCreateRoleHQL());
        run(HiveTableCreate_User_Role_Sell.getCreateSellHQL());
        run(HiveTableCreate_User_Role_Sell.getCreateUserHQL());
        QueryPlanHelper.setHook(hiveConfs.get(getCurKey()));
        HttpServer http = startHTTPServer("queryplan", "localhost", 8080, null);
        http.start();
        hiveCmd();
    }

    public static void analyzeDDL() {

    }

    public static void analyzeDML_Join() {
        
    }
    
    public static void analyzePPD() {
        
    }
    
    public static void analyzeDML_MultiInsert() {

    }
}
