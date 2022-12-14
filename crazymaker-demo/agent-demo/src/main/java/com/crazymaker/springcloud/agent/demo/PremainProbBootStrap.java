package com.crazymaker.springcloud.agent.demo;


import java.lang.instrument.Instrumentation;

public class PremainProbBootStrap {
    public static void premain(String agentArgs) {
        System.out.println("启动时加载探针 ， 此为单参数版本");
        System.out.println("启动时加载探针 ====> " + agentArgs);
    }
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("启动时加载探针 !");
        System.out.println("启动时加载探针 agentArgs ====> " + agentArgs);
        inst.addTransformer(new MethodProb());
    }



}
