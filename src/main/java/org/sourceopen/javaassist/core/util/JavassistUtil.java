package org.sourceopen.javaassist.core.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
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

    public static ClassPool getClassPool(ClassPath[] classPaths, String[] imports) {
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

    public static void replaceMethod(CtClass ctc, String methodName, String newBody, boolean cover)
                                                                                                   throws NotFoundException,
                                                                                                   CannotCompileException,
                                                                                                   IOException {
        CtMethod ctm = ctc.getDeclaredMethod(methodName);
        if (ctm != null) {
            if (!cover) {
                String oldName = ctm.getName();
                ctm.setName(JavassistUtil.getInnerMethodName(ctm.getName()));
                CtMethod newCtm = CtNewMethod.copy(ctm, oldName, ctc, null);
                newCtm.setBody(newBody);
                ctc.addMethod(newCtm);
            } else {
                ctm.setBody(newBody);
            }
        }
    }

    public static void replaceC(CtClass ctc, String methodName, String newBody, boolean cover)
                                                                                                   throws NotFoundException,
                                                                                                   CannotCompileException,
                                                                                                   IOException {
        CtMethod ctm = ctc.getDeclaredMethod(methodName);
        if (ctm != null) {
            if (!cover) {
                String innerMethodName = JavassistUtil.getInnerMethodName(ctm.getName());
                CtMethod newCtm = CtNewMethod.copy(ctm, ctc, null);
                ctm.setName(innerMethodName);
                newCtm.setBody(newBody);
                ctc.addMethod(newCtm);
            } else {
                ctm.setBody(newBody);
            }
        }
    }

    public static void addMethod(CtClass ctc, String methodString) throws NotFoundException, CannotCompileException,
                                                                  IOException {
        if (ctc != null) {
            CtMethod ctm = CtNewMethod.make(methodString, ctc);
            ctc.addMethod(ctm);
        }
    }

    public static void addField(CtClass ctc, String str) throws NotFoundException, CannotCompileException, IOException {
        if (ctc == null) {
            CtField cf = CtField.make(str, ctc);
            ctc.addField(cf);
        }
    }

}
