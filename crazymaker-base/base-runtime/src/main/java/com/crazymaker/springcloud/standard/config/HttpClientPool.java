package com.crazymaker.springcloud.standard.config;

import com.crazymaker.springcloud.standard.properties.HttpConnectionProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
//@Configuration
public class HttpClientPool
{

    @Resource
    private HttpConnectionProperties httpConnectionProperties;


    private ScheduledExecutorService monitorExecutor = null;
    private CloseableHttpClient httpClient;
    private PoolingHttpClientConnectionManager manager;

    /**
     * 创建 带连接池的httpClient 客户端
     */
    @Bean
    public CloseableHttpClient httpClient(HttpClientConnectionManager httpClientConnectionManager)
    {
        log.info(" Apache httpclient 初始化连接池  starting===" );
        // 生成默认请求配置
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        // 等待数据超时设置
        requestConfigBuilder.setSocketTimeout(httpConnectionProperties.getSocketTimeout());
        // 连接超时设置
        requestConfigBuilder.setConnectTimeout(httpConnectionProperties.getConnectTimeout());
        //从连接池获取连接的等待超时时间设置
        requestConfigBuilder.setConnectionRequestTimeout(httpConnectionProperties.getConnectionRequestTimeout());
        RequestConfig config = requestConfigBuilder.build();

        // httpclient 配置
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //设置连接池管理器
        httpClientBuilder.setConnectionManager(httpClientConnectionManager);
        //设置请求配置信息
        httpClientBuilder.setDefaultRequestConfig(config);

        /**
         * 禁止重试
         */
/*
        //方法一
        HttpRequestRetryHandler noTryHandler = new DefaultHttpRequestRetryHandler(0, false);
        httpClientBuilder.setRetryHandler(noTryHandler);
        //方法二
        httpClientBuilder.disableAutomaticRetries();
*/


        //httpclient默认提供了一个Keep-Alive策略,这里进行定制
        httpClientBuilder.setKeepAliveStrategy(new ConnectionKeepAliveStrategy()
        {
            @Override
            public long getKeepAliveDuration(HttpResponse response, HttpContext context)
            {
                //HTTP.CONN_KEEP_ALIVE的为“Keep-Alive”
                HeaderElementIterator it = new BasicHeaderElementIterator
                        (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
                while (it.hasNext())
                {
                    HeaderElement he = it.nextElement();
                    String param = he.getName();
                    String value = he.getValue();
                    if (value != null && param.equalsIgnoreCase
                            ("timeout" ))
                    {
                        try
                        {
                            return Long.parseLong(value) * 1000;
                        } catch (final NumberFormatException ignore)
                        {
                        }
                    }
                }
                //如果没有约定，则默认定义时长为600s
                return httpConnectionProperties.getKeepAliveTimeout();
            }
        });


        this.httpClient = httpClientBuilder.build();

        log.info(" Apache httpclient 初始化连接池  finished===" );

        //启动定时处理线程：对异常和空闲连接进行关闭
        startExpiredConnectionsMonitor();
        return httpClient;
    }

    /**
     * 定时处理线程：对异常和空闲连接进行关闭
     */
    private void startExpiredConnectionsMonitor()
    {
        //空闲监测,配置文件默认为6s,生产环境建议稍微放大一点
        int idleCheckGap = httpConnectionProperties.getIdleCheckGap();
        // 设置保持连接的时长,配置文件默认为600s,根据实际情况调整配置
        int keepAliveTimeout = httpConnectionProperties.getKeepAliveTimeout();
        //开启监控线程,对异常和空闲线程进行关闭
        monitorExecutor = Executors.newScheduledThreadPool(1);
        monitorExecutor.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                //关闭异常连接
                manager.closeExpiredConnections();
                // 可选的, 关闭 活动时间之外 不活动的连接
                manager.closeIdleConnections(keepAliveTimeout, TimeUnit.MILLISECONDS);
                PoolStats status = manager.getTotalStats();
                //输出连接池的状态,仅供测试使用
             /*   log.info(" manager.getRoutes().size():" + manager.getRoutes().size());
                log.info(" status.getAvailable():" + status.getAvailable());
                log.info(" status.getPending():" + status.getPending());
                log.info(" status.getLeased():" + status.getLeased());
                log.info(" status.getMax():" + status.getMax());*/
            }
        }, idleCheckGap, idleCheckGap, TimeUnit.MILLISECONDS);
    }

    //连接池管理器 PoolingHttpClientConnectionManager
    @Bean
    public HttpClientConnectionManager httpClientConnectionManager()
    {

        //DNS解析器
        DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;
        //注册访问协议相关的Socket工厂
        ConnectionSocketFactory plainSocketFactory =
                PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslSocketFactory =
                SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry =
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", plainSocketFactory)
                        .register("https", sslSocketFactory)
                        .build();
        //创建连接池管理器
        manager = new PoolingHttpClientConnectionManager(
                registry,
                null,
                null,
                dnsResolver,
                httpConnectionProperties.getKeepAliveTimeout(),
                TimeUnit.MILLISECONDS);

        //在从连接池获取连接时，连接不活跃多长时间后需要进行一次验证
        // 默认为2s  TimeUnit.MILLISECONDS
        manager.setValidateAfterInactivity(httpConnectionProperties.getValidateAfterInactivity());
        // 设置总连接数
        //高于这个值时，新连接请求，需要阻塞，排队等待
        manager.setMaxTotal(httpConnectionProperties.getMaxTotal());
        // 设置每个route默认的最大连接数
        //路由是对MaxTotal的细分。
        // 每个路由实际最大连接数默认值是由DefaultMaxPerRoute控制。
        // MaxPerRoute设置的过小，无法支持大并发
        manager.setDefaultMaxPerRoute(httpConnectionProperties.getDefaultMaxPerRoute());
        return manager;
    }


    @PreDestroy
    public void destroy() throws Exception
    {

        if (this.httpClient != null)
        {
//            this.httpClient.close();
            manager.close();
            monitorExecutor.shutdown();
        }

    }
}
