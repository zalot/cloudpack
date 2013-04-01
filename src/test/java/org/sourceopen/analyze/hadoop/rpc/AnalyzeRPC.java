package org.sourceopen.analyze.hadoop.rpc;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.Server;
import org.apache.hadoop.ipc.VersionedProtocol;
import org.apache.hadoop.net.NetUtils;
import org.junit.Test;
import org.sourceopen.base.HBaseBase;

/**
 * 类AnalyzeRPC.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Aug 16, 2012 11:13:30 AM
 */
public class AnalyzeRPC extends HBaseBase {

    public static interface IAnalyzeServer extends VersionedProtocol {

        public void print();

        public String getInfo();
    }

    public static class AnalyzeServer implements IAnalyzeServer {

        public AnalyzeServer(){

        }

        public synchronized void print() {
            System.out.println("print " + Thread.currentThread());
        }

        public String getInfo() {
            System.out.println("getInfo " + Thread.currentThread());
            return System.currentTimeMillis() + "";
        }

        @Override
        public long getProtocolVersion(String protocol, long clientVersion) throws IOException {
            return 0;
        }
    }

    public static class UseProxy extends Thread {

        IAnalyzeServer sc;

        public UseProxy(IAnalyzeServer sc){
            this.sc = sc;
        }

        public void run() {
            while (true) {
                sc.getInfo();
                sc.print();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testSingleProxyAndMultThread() throws IOException {
        AnalyzeServer server = new AnalyzeServer();
        Server svr = RPC.getServer(server, "localhost", 3131, new Configuration());
        svr.start();
        final IAnalyzeServer sc = (IAnalyzeServer) RPC.getProxy(IAnalyzeServer.class, 0,
                                                                NetUtils.createSocketAddr("localhost:3131"),
                                                                new Configuration());
        System.out.println(sc.getInfo());

        UseProxy p1 = new UseProxy(sc);
        UseProxy p2 = new UseProxy(sc);

        p1.start();
        p1.start();
    }

    @Test
    public void testMultProxyAndMultThread() throws IOException, InterruptedException {
        AnalyzeServer s = new AnalyzeServer();
        Server svr = RPC.getServer(s, "localhost", 3131, 10, false, new Configuration());
        svr.start();
        final IAnalyzeServer sc1 = (IAnalyzeServer) RPC.getProxy(IAnalyzeServer.class, 0,
                                                                 NetUtils.createSocketAddr("localhost:3131"),
                                                                 new Configuration());

        final IAnalyzeServer sc2 = (IAnalyzeServer) RPC.getProxy(IAnalyzeServer.class, 0,
                                                                 NetUtils.createSocketAddr("localhost:3131"),
                                                                 new Configuration());

        UseProxy p1 = new UseProxy(sc1);
        UseProxy p2 = new UseProxy(sc2);

        p1.start();
        p2.start();
        Thread.sleep(100000L);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        
    }
}
