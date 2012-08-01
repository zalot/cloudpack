package org.sourceopen.hadoop.zookeeper.core;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.ZooKeeper;
import org.sourceopen.javaassist.core.util.JavassistUtil;

/**
 * 类ZNodeProxy.java的实现描述：TODO 类实现描述
 * 
 * @author zalot.zhaoh Aug 1, 2012 10:36:46 AM
 */
public class ZNodeProxy {

    protected final static Log    LOG         = LogFactory.getLog(ZNodeProxy.class);
    protected final static String CLASS_ZNODE = "org.sourceopen.zookeeper.core.ZNode";
    protected static ZooKeeper    zookeeper   = null;

    public static void setProxyZooKeeper(ZooKeeper zk) {
        zookeeper = zk;
    }

    public static ZooKeeper getProxyZooKeeper() {
        return zookeeper;
    }

    public static ZNode createZNode(ZNode parent, String name) {
        ZNode znode = createProxyZNode(parent, name);
        if (znode == null) {
            znode = new ZNode(parent, name) {
            };
        }
        return znode;
    }

    public static ZNode createProxyZNode(ZNode parent, String name) {
        if (zookeeper != null) {
            try {
                ZNodeChangeProxyClass();
                ClassPool cp = ClassPool.getDefault();

            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("can not create AUTOZNode", e);
                }
            }
        } else {
            if (LOG.isWarnEnabled()) {
                LOG.warn("can not create AUTOZNode, please use ZNodeProxy.setProxyZooKeeper(Zookeeper)");
            }
        }
        return null;
    }

    protected static void ZNodeChangeProxyClass() throws NotFoundException, CannotCompileException {
        addZooKeeperProxy();
        addAutoPathGenerator();
        changePathMethod();
    }

    private static void changePathMethod() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("   if (parent != null) {");
        sb.append("      return parent.getPath() + SPLIT + getName();");
        sb.append("   }");
        sb.append("   String rName = SPLIT + getName();");
        sb.append("   proxyPath(rName);");
        sb.append("   return ");
        sb.append("}");
    }

    private static void getAutoPathGenerator() throws NotFoundException, CannotCompileException, IOException {
        String methodString = "proxyPath";
        String[] imports = new String[] { "import org.apache.zookeeper.*" };
        StringBuffer sb = new StringBuffer();
        sb.append("public void proxyPath(String path){");
        sb.append("   zookeeper.exist();");
        sb.append("   ;");
        sb.append("   }");
        sb.append("   String rName = SPLIT + getName();");
        sb.append("   proxyPath(rName);");
        sb.append("   return ");
        sb.append("}");
        JavassistUtil.addMethod(CLASS_ZNODE, methodString, null, imports);
    }

    private static void addZooKeeperProxy() throws NotFoundException, CannotCompileException {
        String methodName = "proxyPath";
        String[] imports = new String[] { "import org.apache.zookeeper.*" };
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("   String path = obj.toString();");
        sb.append("   zookeeper.exist();");
        sb.append("   }");
        sb.append("   String rName = SPLIT + getName();");
        sb.append("   proxyPath(rName);");
        sb.append("   return ");
        sb.append("}");
        JavassistUtil.addMethod(CLASS_ZNODE, methodName, null, null, sb.toString(), null, imports);
    }

    public static void main(String[] args) {
        ZNode a = ZNodeProxy.createZNode(null, "a");
        ZNode b = ZNodeProxy.createZNode(a, "b");
        ZNode c = ZNodeProxy.createZNode(b, "c");

        System.out.println(c.getPath());
    }
}
