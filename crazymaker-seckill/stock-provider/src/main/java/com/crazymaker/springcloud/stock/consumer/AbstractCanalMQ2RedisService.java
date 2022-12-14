package com.crazymaker.springcloud.stock.consumer;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.crazymaker.springcloud.stock.canal.SQLType;
import com.crazymaker.springcloud.standard.redis.RedisRepository;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Set;

import static com.crazymaker.springcloud.stock.canal.CanalUtil.getCanalData;


/**
 * 抽象CanalMQ通用处理服务
 **/

@Slf4j
public abstract class AbstractCanalMQ2RedisService<T> implements CanalSynService<T> {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    RedisRepository redisRepository;


    private Class<T> classCache;

    /**
     * 获取Model名称
     *
     * @return Model名称
     */
    protected abstract String getModelName();

    @Override
    public void process(FlatMessage flatMessage) {

        if (flatMessage.getIsDdl()) {
            ddl(flatMessage);
            return;
        }
        Class<T> type = getTypeArguement();
        Set<T> data = getCanalData(flatMessage,type);

        if (SQLType.INSERT.equals(flatMessage.getType())) {
            insert(data);
        }

        if (SQLType.UPDATE.equals(flatMessage.getType())) {
            update(data);
        }

        if (SQLType.DELETE.equals(flatMessage.getType())) {
            delete(data);
        }

    }

    @Override
    public void ddl(FlatMessage flatMessage) {
        //TODO : DDL需要同步，删库清空，更新字段处理

    }

    @Override
    public void insert(Collection<T> list) {
        insertOrUpdate(list);
    }

    @Override
    public void update(Collection<T> list) {
        insertOrUpdate(list);
    }

    private void insertOrUpdate(Collection<T> list) {
        redisTemplate.executePipelined((RedisConnection redisConnection) -> {
            for (T data : list) {
                String key = getWrapRedisKey(data);
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
                redisConnection.set(keySerializer.serialize(key), valueSerializer.serialize(data));
            }
            return null;
        });
    }

    @Override
    public void delete(Collection<T> list) {

        Set<String> keys = Sets.newHashSetWithExpectedSize(list.size());

        for (T data : list) {
            keys.add(getWrapRedisKey(data));
        }

        //Set<String> keys = list.stream().map(this::getWrapRedisKey).collect(Collectors.toSet());
        redisRepository.delAll(keys);
    }

    /**
     * 封装redis的key
     *
     * @param t 原对象
     * @return key
     */
    protected String getWrapRedisKey(T t) {
//        return new StringBuilder()
//                .append(ApplicationContextHolder.getApplicationName())
//                .append(":")
//                .append(getModelName())
//                .append(":")
//                .append(getIdValue(t))
//                .toString();

        throw new IllegalStateException(
                "基类 方法 'getWrapRedisKey' 尚未实现!");
    }

    /**
     * 获取类泛型
     *
     * @return 泛型Class
     */
    protected Class<T> getTypeArguement() {
        if (classCache == null) {
            classCache = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return classCache;
    }


}