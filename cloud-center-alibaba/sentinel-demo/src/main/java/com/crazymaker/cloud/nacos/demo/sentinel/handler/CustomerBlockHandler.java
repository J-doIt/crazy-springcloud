package com.crazymaker.cloud.nacos.demo.sentinel.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerBlockHandler {
    public String userAccessError(String userId, String skuId, BlockException exception) {
        log.info(Thread.currentThread().getName() + "\t" + "...userAccessError");
        return "------userAccessError，i am from  test5";
    }


    // Block 异常处理函数，参数最后多一个 BlockException，其余与原函数一致.
    public String exceptionHandler(long num, BlockException ex) {
        // Do some log here.
        ex.printStackTrace();
        log.info(Thread.currentThread().getName() + "\t" + "...exceptionHandler");
        return String.format("error: input num  %d is not OK", num);
    }


}
