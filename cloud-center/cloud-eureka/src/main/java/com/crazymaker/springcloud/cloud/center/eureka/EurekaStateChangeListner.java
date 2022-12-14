package com.crazymaker.springcloud.cloud.center.eureka;

import com.netflix.appinfo.InstanceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaServerStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EurekaStateChangeListner {

    /**
     * 服务上线 事件
     */
    @EventListener
    public void listen(EurekaInstanceRegisteredEvent event){
        InstanceInfo inst = event.getInstanceInfo();
        log.info("{}:{} \t {} 服务上线",
                inst.getIPAddr(),inst.getPort(),inst.getAppName());
    }

    /**
     *  服务下线事件  
     */
    @EventListener
    public void listen(EurekaInstanceCanceledEvent event){
        log.info("{} \t {} 服务下线",event.getServerId(),event.getAppName());
    }


    /**
     * 服务续约(服务心跳) 事件
     */
    @EventListener
    public void listen(EurekaInstanceRenewedEvent event){
        log.info("{} \t {} 服务续约",event.getServerId(),event.getAppName());
    }

    @EventListener
    public void listen(EurekaServerStartedEvent event){
        log.info("Eureka Server 启动");
    }
}
