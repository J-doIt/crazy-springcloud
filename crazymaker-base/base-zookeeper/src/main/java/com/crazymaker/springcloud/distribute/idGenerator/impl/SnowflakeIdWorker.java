package com.crazymaker.springcloud.distribute.idGenerator.impl;

import com.crazymaker.springcloud.distribute.zookeeper.ZKClient;
import lombok.Data;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

/**
 * build by 尼恩 @ 疯狂创客圈
 **/
@Data
public class SnowflakeIdWorker
{


    //Zk客户端
    private CuratorFramework client = null;

    //工作节点的路径
    private String pathPrefix = "/test/IDMaker/worker-";
    private String pathRegistered = null;
    private  boolean inited=false;


    public SnowflakeIdWorker()
    {
        client = ZKClient.getSingleton().getClient();

    }


    public SnowflakeIdWorker(String type)
    {
        pathPrefix = "/snowflakeId/" + type + "/worker-";

    }


    // 在zookeeper中创建临时节点并写入信息
    public void init()
    {
        client = ZKClient.getSingleton().getClient();

        // 创建一个 ZNode 节点
        // 节点的 payload 为当前worker 实例

        try
        {
//            byte[] payload = JsonUtil.object2JsonBytes(this);

            pathRegistered = client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(pathPrefix);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        inited=true;
    }

    public long getId()
    {
        if(!inited)
        {
            init();
        }
        String sid = null;
        if (null == pathRegistered)
        {
            throw new RuntimeException("节点注册失败" );
        }
        int index = pathRegistered.lastIndexOf(pathPrefix);
        if (index >= 0)
        {
            index += pathPrefix.length();
            sid = index <= pathRegistered.length() ? pathRegistered.substring(index) : null;
        }

        if (null == sid)
        {
            throw new RuntimeException("节点ID生成失败" );
        }

        return Long.parseLong(sid);

    }
}