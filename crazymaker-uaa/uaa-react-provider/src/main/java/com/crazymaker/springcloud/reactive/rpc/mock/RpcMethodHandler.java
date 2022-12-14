package com.crazymaker.springcloud.reactive.rpc.mock;

/**
 * RPC 方法处理器
 */
interface RpcMethodHandler
{

    /**
     * 功能：组装 url，完成 REST RPC 远程调用，并且返回 JSON结果
     *
     * @param argv RPC 方法的参数
     * @return REST 接口的响应结果
     * @throws Throwable 异常
     */
    Object invoke(Object[] argv) throws Throwable;
}
