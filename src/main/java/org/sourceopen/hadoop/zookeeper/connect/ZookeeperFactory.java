package org.sourceopen.hadoop.zookeeper.connect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.CtPrimitiveType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZookeeperFactory {

    protected static final Log    LOG              = LogFactory.getLog(ZookeeperFactory.class);
    protected static String       CLASS_ZOOKEEPER  = "org.apache.zookeeper.ZooKeeper";
    protected static List<String> RECOVERY_METHODS = new ArrayList<String>();
    protected static Class        FINAL_CLASS      = null;

    public static void setRecoveryMethods(List<String> methods) {
        RECOVERY_METHODS = methods;
    }

    public static List<String> getRecoveryMethods() {
        return RECOVERY_METHODS;
    }

    static {
        // DEFAULT setRecoveryMethods
        RECOVERY_METHODS = Arrays.asList(new String[] { "create", "exists", "delete", "getChildren", "getData",
            "setData" });
    }

    public static ZooKeeper createRecoverableZooKeeper(String url, int timeout, int rt, Watcher watcher)
                                                                                                        throws Exception {
        if (FINAL_CLASS == null) {
            ClassPool cp = ClassPool.getDefault();
            // ****************************************************************************
            cp.importPackage("org.apache.zookeeper.KeeperException");
            // ****************************************************************************
            CtClass ctClass = cp.get(CLASS_ZOOKEEPER);
            CtMethod[] ctMethods = ctClass.getDeclaredMethods();
            for (CtMethod method : ctMethods) {
                if (!RECOVERY_METHODS.contains(method.getName())) continue;
                String oldName = method.getName();
                String innerMethodName = oldName + "_inner_impl";
                method.setName(innerMethodName);
                CtMethod nm = CtNewMethod.copy(method, oldName, ctClass, null);
                if (!nm.getReturnType().equals(CtPrimitiveType.voidType)) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("{");
                    sb.append("  " + nm.getReturnType().getName() + " tmp = null;");
                    sb.append("  long baseTime = 100;");
                    sb.append("  int curC = 0;");
                    sb.append("  while(true){");
                    sb.append("      try{ ");
                    sb.append("           tmp = " + innerMethodName + "($$);");
                    sb.append("           return tmp;");
                    sb.append("      }catch(KeeperException e){");
                    sb.append("         if(e.code() == KeeperException.Code.CONNECTIONLOSS ");
                    sb.append("         || e.code() == KeeperException.Code.OPERATIONTIMEOUT) {");
                    sb.append("              System.out.println(e + \" wait time [\" + baseTime + \"]ms\"); ");
                    sb.append("              Thread.sleep(baseTime);");
                    sb.append("         }else{");
                    sb.append("              throw e;");
                    sb.append("         }");
                    sb.append("      }");
                    sb.append("      curC++;if(curC>" + rt + ") throw new Exception(\"no...\");");
                    sb.append("      baseTime = baseTime * curC;");
                    sb.append("  }");
                    sb.append("}");
                    nm.setBody(sb.toString());
                } else {
                    nm.setBody("{" + innerMethodName + "($$);}");
                }
                ctClass.addMethod(nm);
            }
            // ****************************************************************************
            String constructBody = "{this(\"" + url + "\", " + timeout + ", null);}";
            CtConstructor ctc = new CtConstructor(new CtClass[0], ctClass);
            ctc.setBody(constructBody);
            ctClass.addConstructor(ctc);
            ctClass.writeFile();
            // ****************************************************************************
            FINAL_CLASS = ctClass.toClass();
        }
        ZooKeeper cc = (ZooKeeper) FINAL_CLASS.newInstance();
        if (watcher != null) {
            cc.register(watcher);
        }
        return cc;
    }

    public static void main(String[] args) throws Exception {
        ZooKeeper zk = ZookeeperFactory.createRecoverableZooKeeper("h7:2181", 1000, 11, new NothingZookeeperWatch());
        long st = System.currentTimeMillis();
        for (int x = 0; x < 10; x++)
            zk.getChildren("/", false);
        long en = System.currentTimeMillis();
        System.out.println(en - st);
        // zk.create("/", "no".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }
}
