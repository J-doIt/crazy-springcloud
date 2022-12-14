package com.crazymaker.springcloud.standard.config;

import com.crazymaker.springcloud.standard.properties.HttpConnectionProperties;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({Connector.class})
public class TomcatConfig
{

    @Autowired
    private HttpConnectionProperties httpConnectionProperties;


    @Bean
    public TomcatServletWebServerFactory createEmbeddedServletContainerFactory()
    {
        TomcatServletWebServerFactory tomcatFactory = new TomcatServletWebServerFactory();
        // tomcat的配置可以在这里加
        // public static final String DEFAULT_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";
        tomcatFactory.addConnectorCustomizers(connector ->
        {
            Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
            // 定制化keepAliveTimeout，确定下次请求过来之前，socket链接保持多久
            // 设置600秒内没有请求则服务端自动断开keepalive链接,
            protocol.setKeepAliveTimeout(httpConnectionProperties.getKeepAliveTimeout());
            // 当客户端发送超过10000个请求,请求个数超过这个数，强制关闭掉socket链接
            protocol.setMaxKeepAliveRequests(httpConnectionProperties.getTomcatMaxAlive());

            //tomcat 设置最大连接数
            protocol.setMaxConnections(httpConnectionProperties.getTomcatMaxConnections());
            //tomcat 设置最大线程数
            protocol.setMaxThreads(httpConnectionProperties.getTomcatMaxThreads());
            protocol.setConnectionTimeout(httpConnectionProperties.getTomcatConnectionTimeout());
            //accept-count：最大排队数
//            protocol.setAcceptCount(httpConnectionProperties.getTomcatAcceptCount());


            //需要发送更大请求的问题，然后允许默认大小：
            int maxSize = 50000000;
            connector.setMaxPostSize(maxSize);
            connector.setMaxSavePostSize(maxSize);
            if (protocol instanceof AbstractHttp11Protocol)
            {
                protocol.setMaxSwallowSize(maxSize);
            }

        });
        return tomcatFactory;
    }
}
