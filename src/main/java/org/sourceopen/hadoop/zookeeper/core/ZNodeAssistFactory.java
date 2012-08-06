package org.sourceopen.hadoop.zookeeper.core;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.ZooKeeper;

public class ZNodeAssistFactory {

    protected final static Log      LOG        = LogFactory.getLog(ZNodeProxy.class);
    protected final static String   CLASS      = "org.sourceopen.hadoop.zookeeper.core.ZNode";
    protected final static String[] IMPORTS    = new String[] { "import org.apache.zookeeper.*" };
    protected static ZooKeeper      zookeeper  = null;
    protected static Constructor    proxyZNode = null;

    //
    // @SuppressWarnings("rawtypes")
    // public static ZNodeDefault createZNode(ZNodeDefault parent, String name) {
    // if (zookeeper != null) {
    // try {
    // if (proxyZNode == null) {
    // Class c = getProxyClass();
    // Constructor con = c.getDeclaredConstructor(ZNodeDefault.class, String.class);
    // proxyZNode = con;
    // }
    // return (ZNodeDefault) proxyZNode.newInstance(parent, name);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // return new ZNodeDefault(parent, name) {
    // };
    // }
    //
    // protected static Class getProxyClass() throws NotFoundException, CannotCompileException, IOException {
    // ClassPool cp = JavassistUtil.getClassPool(null, null);
    // CtClass ctc = cp.get(CLASS);
    // CtClass proxyCtc = cp.makeClass("ProxyZNode");
    // proxyCtc.setSuperclass(ctc);
    // addProxyGetData(proxyCtc);
    // proxyCtc.writeFile();
    // return proxyCtc.toClass();
    // }
    //
    // protected static void adddProxyPath(CtClass ctc) throws NotFoundException, CannotCompileException, IOException {
    // StringBuffer sb = new StringBuffer();
    // sb.append("public void getPath(){");
    // sb.append("   Stat stat = zookeepr.exists(path, false);");
    // sb.append("   ;");
    // sb.append("   }");
    // sb.append("   String rName = SPLIT + getName();");
    // sb.append("   proxyPath(rName);");
    // sb.append("   return ");
    // sb.append("}");
    // JavassistUtil.addMethod(ctc, sb.toString());
    // }
    //
    // protected static void addProxyGetData(CtClass ctc) throws NotFoundException, CannotCompileException, IOException
    // {
    // StringBuffer sb = new StringBuffer();
    // sb.append("public byte[] getDate(){");
    // // sb.append("   if (parent != null) {");
    // // sb.append("      return parent.getPath() + SPLIT + getName();");
    // // sb.append("   }");
    // sb.append("   String path = super.getPath()");
    // sb.append("   Stat stat = zk.exists(path,false);");
    // sb.append("   if(stat != null){ ");
    // sb.append("       return zk.getData(path, false, stat)");
    // sb.append("   }");
    // sb.append("   return null;");
    // sb.append("}");
    // JavassistUtil.addMethod(ctc, sb.toString());
    // }
    //
    // public static void setProxyZooKeeper(ZooKeeper zk) {
    // zookeeper = zk;
    // }
    //
    // public static ZooKeeper getProxyZooKeeper() {
    // return zookeeper;
    // }
    //
    // public static ZNodeDefault createProxyZNode(ZNodeDefault parent, String name) {
    // if (zookeeper != null) {
    // try {
    // // JavassistUtil.getClass(CLASS).newInstance();
    // } catch (Exception e) {
    // if (LOG.isErrorEnabled()) {
    // LOG.error("can not create AUTOZNode", e);
    // }
    // }
    // } else {
    // if (LOG.isWarnEnabled()) {
    // LOG.warn("can not create AUTOZNode, please use ZNodeProxy.setProxyZooKeeper(Zookeeper)");
    // }
    // }
    // return null;
    // }

    public static void main(String[] args) throws Exception {
        // setProxyZooKeeper(ZookeeperFactory.createRecoverableZooKeeper("h7:2181", 1000, 11, new
        // NothingZookeeperWatch()));
        // ZNodeDefault a = createZNode(null, "a");
        // ZNode b = ZNodeProxy.createZNode(a, "b");
        // ZNode c = ZNodeProxy.createZNode(b, "c");
        // System.out.println(a.getPath());
    }
}
