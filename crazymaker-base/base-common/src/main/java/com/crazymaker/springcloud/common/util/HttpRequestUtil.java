package com.crazymaker.springcloud.common.util;

import com.crazymaker.springcloud.standard.context.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class HttpRequestUtil
{
    // 发送请求的客户端单例
    private static CloseableHttpClient httpClient;

    /**
     * 从Spring 容器中，取得带连接池的HttpClient 客户端实例
     *
     * @return HttpClient
     */
    private static CloseableHttpClient getPooledHttpClient()
    {
        if (null == httpClient)
        {
            httpClient = (CloseableHttpClient) SpringContextUtil.getBean("httpClient");
        }
        return httpClient;
    }

    /**
     * 使用连接池中的请求发送
     *
     * @param url 连接地址
     * @return 请求字符串
     */
    public static String get(String url)
    {
        //取得连接池
        CloseableHttpClient client = getPooledHttpClient();
        HttpGet httpGet = new HttpGet(url);
        return poolRequestData(url, client, httpGet);
    }

    /**
     * 1 直接创建客户端
     *
     * @return HttpClient
     */
    private static CloseableHttpClient getDirectHttpClient()
    {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        return client;
    }

    /**
     * 2 简单的发送请求
     *
     * @param url 连接地址
     * @return 请求字符串
     */
    public static String simpleGet(String url) throws IOException
    {
        // 1 直接创建客户端
        CloseableHttpClient client = HttpClientBuilder.create().build();
        //2 创建请求
        HttpGet httpGet = new HttpGet(url);
        //3 超时配置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(1000)
                .setSocketTimeout(1000)
                .setConnectTimeout(1000)
                .build();
        httpGet.setConfig(requestConfig);
        //4 发送请求，处理响应
        return simpleRequestData(url, client, httpGet);
    }

    /**
     * 设置post请求的参数
     *
     * @param httpPost 主机ip和端口
     * @param params   请求参数
     */
    private static void setPostParams(HttpPost httpPost, Map<String, String> params)
    {
        if (null == params)
        {
            return;
        }
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        Set<String> keys = params.keySet();
        for (String key : keys)
        {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }
        try
        {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 使用连接池中的请求发送
     *
     * @param url    连接地址
     * @param params 参数
     * @return 请求字符串
     */
    public static String post(String url, Map<String, String> params)
    {
        //取得连接池
        CloseableHttpClient client = getPooledHttpClient();
        HttpPost httpPost = new HttpPost(url);
        setPostParams(httpPost, params);
        return poolRequestData(url, client, httpPost);
    }

    /**
     * 直接发送请求
     *
     * @param url    连接地址
     * @param params 参数
     * @return 请求字符串
     */
    public static String simplePost(String url, Map<String, String> params) throws IOException
    {
        //取得连接池
        CloseableHttpClient client = getDirectHttpClient();
        HttpPost httpPost = new HttpPost(url);
        setPostParams(httpPost, params);
        return simpleRequestData(url, client, httpPost);
    }


    /**
     * 内部的请求发送
     *
     * @param url     连接地址
     * @param client  客户端
     * @param request post 或者 getStr 或者其他请求
     * @return 请求字符串
     */
    private static String simpleRequestData(String url, CloseableHttpClient client, HttpRequest request) throws IOException
    {
        CloseableHttpResponse response = null;
        InputStream in = null;
        String result = null;
        try
        {
            HttpHost httpHost = getHost(url);
            response = client.execute(httpHost, request, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                in = entity.getContent();
                result = IOUtils.toString(in, "utf-8");
            }
        } finally
        {
            quietlyClose(in);
            quietlyClose(response);
            //释放HttpClient 连接。
            quietlyClose(client);
        }

        return result;
    }


    /**
     * 内部的请求发送
     *
     * @param url     连接地址
     * @param client  客户端
     * @param request post 或者 getStr 或者其他请求
     * @return 请求字符串
     */
    private static String poolRequestData(String url, CloseableHttpClient client, HttpRequest request)
    {
        CloseableHttpResponse response = null;
        InputStream in = null;
        String result = null;
        try
        {
            HttpHost httpHost = getHost(url);
            response = client.execute(httpHost, request, HttpClientContext.create());
            HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                in = entity.getContent();
                result = IOUtils.toString(in, "utf-8");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            quietlyClose(in);
            quietlyClose(response);

            //无论执行成功或出现异常，HttpClient 都会自动处理并保证释放连接。

        }

        return result;
    }


    /**
     * 从url 中获取主机
     *
     * @param url url 地址
     * @return HttpHost
     */
    private static HttpHost getHost(String url)
    {
        String hostName = url.split("/")[2];
        int port = 80;
        if (hostName.contains(":"))
        {
            String[] args = hostName.split(":");
            hostName = args[0];
            port = Integer.parseInt(args[1]);
        }
        HttpHost httpHost = new HttpHost(hostName, port);
        return httpHost;
    }

    /**
     * 安静的关闭可关闭对象
     *
     * @param closeable 可关闭对象
     */
    private static void quietlyClose(java.io.Closeable closeable)
    {
        if (null == closeable) return;
        try
        {
            closeable.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}