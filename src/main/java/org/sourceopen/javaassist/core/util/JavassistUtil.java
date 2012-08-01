package org.sourceopen.javaassist.core.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.sourceopen.javaassist.JavassistConstants;

public class JavassistUtil {

    protected static final Map<String, CtClass> CLASSPOOL       = new HashMap<String, CtClass>();
    protected static final Map<String, Class>   FINAL_CLASSPOOL = new HashMap<String, Class>();

    public static String getInnerMethodName(String name) {
        return name + JavassistConstants.INNER_METHOD_SUFFIX;
    }

    public static ClassPool getClassPool(String className, ClassPath[] classPaths, String[] imports) {
        ClassPool cp = ClassPool.getDefault();
        if (classPaths != null) {
            for (ClassPath cps : classPaths)
                cp.appendClassPath(cps);
        }
        if (imports != null) {
            for (String i : imports)
                cp.importPackage(i);
        }
        return cp;
    }

    public static boolean replaceMethod(String className, String methodName, String newBody) throws NotFoundException,
                                                                                            CannotCompileException {
        return replaceMethod(className, methodName, newBody, false, null, null);
    }

    public static boolean replaceMethod(String className, String methodName, String newBody, boolean cover,
                                        ClassPath[] classPaths, String[] imports) throws NotFoundException,
                                                                                 CannotCompileException {
        CtClass ctc = CLASSPOOL.get(className);
        if (ctc == null) {
            ClassPool cp = getClassPool(className, classPaths, imports);
            ctc = cp.get(className);
            CLASSPOOL.put(className, ctc);
            CtMethod ctm = ctc.getDeclaredMethod(methodName);
            if (ctm != null) {
                if (!cover) {
                    CtMethod newCtm = CtNewMethod.copy(ctm, ctm.getName(), ctc, null);
                    String innerMethodName = JavassistUtil.getInnerMethodName(ctm.getName());
                    ctm.setName(innerMethodName);
                    newCtm.setBody(newBody);
                    ctc.addMethod(newCtm);
                } else {
                    ctm.setBody(newBody);
                }
                return true;
            }
        }
        return false;
    }

    public static Class addMethod(String className, String methodString, ClassPath[] classPaths, String[] imports)
                                                                                                                  throws NotFoundException,
                                                                                                                  CannotCompileException,
                                                                                                                  IOException {
        CtClass ctc = CLASSPOOL.get(className);
        if (ctc == null) {
            ClassPool cp = getClassPool(className, classPaths, imports);
            ctc = cp.get(className);
            CtMethod ctm = CtNewMethod.make(methodString, ctc);
            ctc.addMethod(ctm);
            ctc.writeFile();
            Class finalClass = ctc.toClass();
            FINAL_CLASSPOOL.put(className, finalClass);
            return finalClass;
        }
        return null;
    }

    public static Class getClass(String name) {
        return FINAL_CLASSPOOL.get(name);
    }
}
