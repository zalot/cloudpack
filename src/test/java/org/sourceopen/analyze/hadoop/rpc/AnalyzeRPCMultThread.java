package org.sourceopen.analyze.hadoop.rpc;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.Server;
import org.sourceopen.analyze.hadoop.rpc.AnalyzeRPC.AnalyzeServer;

public class AnalyzeRPCMultThread {

    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        AnalyzeServer s = new AnalyzeServer();
//        Server svr = RPC.getServer(s, "localhost", 3131, 10, false, );
    }
}
