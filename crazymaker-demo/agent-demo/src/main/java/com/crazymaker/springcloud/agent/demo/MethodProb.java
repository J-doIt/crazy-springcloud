package com.crazymaker.springcloud.agent.demo;

import javassist.*;
import javassist.bytecode.CodeAttribute;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class MethodProb implements ClassFileTransformer {

    //目标类名称，.分隔
    public static final String METHOD_TO_PROBE ="com.crazymaker.springcloud";
    @Override

    //类加载时会执行该函数，
    // 其中参数 classfileBuffer为类原始字节码，
    // 参数className为/分隔
    // 返回值为目标字节码

    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer)
            throws IllegalClassFormatException {

        //把"/"替换成"."
        className = className.replace("/", ".");


        //判断类名是否为目标类名
        if(className.startsWith(METHOD_TO_PROBE)){
            //使用className,获取字节码的类
            CtClass ctClass = null;

            try {
                ctClass = ClassPool.getDefault().get(className);
                CtMethod[] ctMethods = ctClass.getMethods();
                for(CtMethod ctMethod: ctMethods){
                    CodeAttribute ca = ctMethod.getMethodInfo2().getCodeAttribute();
                    if(ca == null){
                        continue;
                    }
                    if(!ctMethod.isEmpty()){
                        ctMethod.insertBefore("System.out.println(\" 此方法被探针拦截 :"+ctMethod.getName()+"\");");
                        ctMethod.insertAfter("{ System.out.println(\"探针拦截 end\"); }");

                    }
                }
                return ctClass.toBytecode();
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
