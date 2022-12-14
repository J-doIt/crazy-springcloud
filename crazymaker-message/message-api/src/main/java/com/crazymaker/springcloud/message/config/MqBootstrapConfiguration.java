package com.crazymaker.springcloud.message.config;

import com.crazymaker.springcloud.message.core.MqListenerEndpointRegistry;
import com.crazymaker.springcloud.message.core.MqListenerPostProcessor;
import com.crazymaker.springcloud.message.core.RabbitMqEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "application.message.queue", name = "consumer", havingValue = "true" )
@EnableConfigurationProperties(MqConfigProperties.class)
public class MqBootstrapConfiguration
{

    private final MqConfigProperties configProperties;

    @Autowired
    public MqBootstrapConfiguration(MqConfigProperties configProperties)
    {
        this.configProperties = configProperties;
    }

    @Bean
//    @ConditionalOnExpression("${application.message.queue.consumer:true}")
    public MqListenerPostProcessor defaultMqListenerPostProcessor()
    {
        return new MqListenerPostProcessor();
    }

    @Bean
    public MqListenerEndpointRegistry defaultMqListenerEndpointRegistry()
    {
        return new MqListenerEndpointRegistry();
    }


    @Bean
    public RabbitMqEndpoint defaultMqEndpoint()
    {
        return new RabbitMqEndpoint(configProperties);
    }


}
