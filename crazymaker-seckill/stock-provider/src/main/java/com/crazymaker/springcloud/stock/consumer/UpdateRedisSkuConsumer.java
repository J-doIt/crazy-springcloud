package com.crazymaker.springcloud.stock.consumer;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.crazymaker.springcloud.seckill.dao.po.SeckillSkuPO;
import com.google.common.collect.Sets;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
//广播模式
//@RocketMQMessageListener(topic = "seckillsku", consumerGroup = "UpdateRedis", messageModel = MessageModel.BROADCASTING)
//集群模式
@RocketMQMessageListener(topic = "seckillsku", consumerGroup = "UpdateRedis")
@Data
public class UpdateRedisSkuConsumer extends AbstractCanalMQ2RedisService<SeckillSkuPO> implements RocketMQListener<FlatMessage> {

    private String modelName = "seckillsku";

    @Override
    public void onMessage(FlatMessage s) {
        process(s);
    }

//    @Cacheable(cacheNames = {"seckill"}, key = "'seckillsku:' + #skuId")

    /**
     * 封装redis的key
     *
     * @param t 原对象
     * @return key
     */
    protected String getWrapRedisKey(SeckillSkuPO t) {
        return new StringBuilder()
//                .append(ApplicationContextHolder.getApplicationName())
                .append("seckill")
                .append(":")
//                .append(getModelName())
                .append("seckillsku")
                .append(":")
                .append(t.getId())
                .toString();

    }

    /**
     * 转换Canal的FlatMessage中data成泛型对象
     *
     * @param flatMessage Canal发送MQ信息
     * @return 泛型对象集合
     */
    protected Set<SeckillSkuPO> getCanalData(FlatMessage flatMessage) {
        List<Map<String, String>> sourceData = flatMessage.getData();
        Set<SeckillSkuPO> targetData = Sets.newHashSetWithExpectedSize(sourceData.size());
        for (Map<String, String> map : sourceData) {
            SeckillSkuPO po = new SeckillSkuPO();
            po.setId(Long.valueOf(map.get("id")));
            //省略其他的属性
            targetData.add(po);
        }
        return targetData;
    }

}