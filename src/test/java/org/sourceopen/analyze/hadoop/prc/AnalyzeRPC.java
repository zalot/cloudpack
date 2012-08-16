package org.sourceopen.analyze.hadoop.prc;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.Server;
import org.apache.hadoop.ipc.VersionedProtocol;
import org.apache.hadoop.net.NetUtils;

/**
 * 类AnalyzeRPC.java的实现描述：TODO 类实现描述 
 * @author zalot.zhaoh Aug 16, 2012 11:13:30 AM
 */
public class AnalyzeRPC {

    public static interface IAnalyzeServer extends VersionedProtocol {

        public void print();

        public String getInfo();
    }

    public static class AnalyzeServer implements IAnalyzeServer {

        public AnalyzeServer(){

        }

        public void print() {

        }

        public String getInfo() {
            return System.currentTimeMillis() + "";
        }

        @Override
        public long getProtocolVersion(String protocol, long clientVersion) throws IOException {
            return 0;
        }
    }

    public static void main(String[] args) throws IOException {
        AnalyzeServer s = new AnalyzeServer();
        Server svr = RPC.getServer(s, "localhost", 3131, new Configuration());
        svr.start();
        IAnalyzeServer sc = (IAnalyzeServer) RPC.getProxy(IAnalyzeServer.class, 0,
                                                          NetUtils.createSocketAddr("localhost:3131"),
                                                          new Configuration());
        System.out.println(sc.getInfo());
    }
}
