package org.sourceopen.hadoop.hive.ql.http;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.ql.QueryPlan;
import org.apache.hadoop.hive.ql.hooks.ExecuteWithHookContext;
import org.apache.hadoop.hive.ql.hooks.HookContext;

public class QueryPlanHelper {

    private static QueryPlanCallBack callback = null;
    private static boolean           isset    = false;

    public static interface QueryPlanCallBack {

        void call(QueryPlan qp);
    }

    public static class NoException extends Exception {

    }

    public static class QueryPlanPerHook implements ExecuteWithHookContext {

        @Override
        public void run(HookContext hookContext) throws Exception {
            System.out.println("----------------------");
            if (QueryPlanHelper.callback != null) QueryPlanHelper.callback.call(hookContext.getQueryPlan());
            throw new NoException();

        }
    }

    public static void setCallBack(QueryPlanCallBack call) {
        callback = call;
    }

    public static void setHook(Configuration conf) {
        String hooks = conf.get(HiveConf.ConfVars.PREEXECHOOKS.varname);
        if (hooks == null || hooks.trim().length() == 0) {
            conf.set(HiveConf.ConfVars.PREEXECHOOKS.varname, QueryPlanPerHook.class.getName());
        } else {
            if(hooks.indexOf(QueryPlanPerHook.class.getName()) >-1) return;
            conf.set(HiveConf.ConfVars.PREEXECHOOKS.varname, hooks + "," + QueryPlanPerHook.class.getName());
        }
    }

    public static void cleanHook(Configuration conf) {
        String hooks = conf.get(HiveConf.ConfVars.PREEXECHOOKS.varname);
        if (hooks != null && hooks.trim().length() >0) {
            conf.set(HiveConf.ConfVars.PREEXECHOOKS.varname, hooks.replace(QueryPlanPerHook.class.getName(), ""));
        }
        isset = true;
    }
}
