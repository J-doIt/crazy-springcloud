package com.crazymaker.springcloud.standard.redis;

import com.crazymaker.springcloud.common.context.SessionHolder;
import com.crazymaker.springcloud.standard.context.SpringContextUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class RedisRepository {
    private RedisTemplate redisTemplate;

    private static final int DefaultTime = 1000;

    public RedisRepository(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

    }

    /**
     * value 序列化
     */
    private static final JdkSerializationRedisSerializer OBJECT_SERIALIZER =
            new JdkSerializationRedisSerializer();

    static RedisRepository service = null;

    //静态获取spring bean
    public static RedisRepository singleton() {
        if (null == service) {
            service = SpringContextUtil.getBean(RedisRepository.class);
            Assert.notNull(service, "RedisService bean must be specified");
        }
        return service;
    }

    /**
     * redis List数据结构 : 返回列表 key 的长度 ; 如果 key 不存在，则 key 被解释为一个空列表，返回 0 ; 如果 key 不是列表类型，返回一个错误。
     *
     * @param key the key
     * @return the long
     */
    public Long length(String key) {
        return opsForList().size(key);
    }

    /**
     * redis List 引擎
     *
     * @return the list operations
     */
    public ListOperations<String, Object> opsForList() {
        return redisTemplate.opsForList();
    }

    /**
     * redis List数据结构 : 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 end 指定。
     *
     * @param key   the key
     * @param start the start
     * @param end   the end
     * @return the list
     */
    public List<Object> getList(String key, int start, int end) {
        return opsForList().range(key, start, end);
    }

    /**
     * 添加key到redis数据库中
     */
    public void set(String key, String value) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(key, value);
    }

    /**
     * 添加key到redis数据库中
     */
    public void set(String key, int value) {
        ValueOperations<String, Integer> operations = redisTemplate.opsForValue();
        operations.set(key, value);
    }

    public void set(String key, String value, int timeOut) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set(key, value, timeOut, TimeUnit.SECONDS);
    }

    /**
     * 刷新过期时间
     *
     * @param key     key
     * @param timeOut timeOut
     */
    public void expire(String key, int timeOut) {
        redisTemplate.expire(key, timeOut, TimeUnit.SECONDS);
    }

    /**
     * 取值key到redis数据库中
     */
    public String getStr(String key) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    public void setBit(String key, long index, boolean val) {
        redisTemplate.opsForValue().setBit(key, index, val);
    }

    public Boolean getBit(String key, long index) {
        return redisTemplate.opsForValue().getBit(key, index);
    }


    /**
     * 删除指定key
     */
    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 保存obj对象到redis数据库
     */
    public void setObj(Object obj) {
        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();
        operations.set(obj.getClass().getName(), obj);
    }

    public void setObj(Object obj, int timeOut) {
        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();
        int times = 0;
        if (timeOut > 0) {
            times = timeOut * 60;
        } else {
            times = DefaultTime;
        }
        operations.set(obj.getClass().getName(), obj, times, TimeUnit.SECONDS);
    }

    /**
     * 根据指定o获取Object
     */
    public <T> T getObj(Object obj, Class<T> clazz) {

        ValueOperations<Object, Object> operations = redisTemplate.opsForValue();
        return (T) operations.get(obj.getClass().getName());
    }

    /**
     * 删除obj对象在redis数据库
     */
    public void delObj(Object o) {
        redisTemplate.delete(o);
    }

    /**
     * Set集合的赋值去取
     */
    public void setSetCollections(String key, Set value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public String getSetCollections(String key) {
        String result = new Gson().toJson(redisTemplate.opsForSet().members(key));
        return result.substring(1, result.length() - 1);
    }

    /**
     * @param key
     * @return
     */
    public Set<String> hKeys(String key) {
        Set<String> resultMapSet = redisTemplate.opsForHash().keys(key);

        return resultMapSet;
    }

    public Set<?> getCollection(String key) {
        String result = new Gson().toJson(redisTemplate.opsForSet().members(key));
        System.out.println("#######RedisService:" + result);
        Set<?> resultSet = redisTemplate.opsForSet().members(key);
        return resultSet;
    }


    /**
     * hash的赋值去设置
     *
     * @param key   key
     * @param hkey  hkey
     * @param value value
     */
    public void hset(String key, String hkey, String value) {
        redisTemplate.opsForHash().put(key, hkey, value);
    }

    /**
     * hash的赋值去取值
     *
     * @param key  key
     * @param hkey hkey
     */
    public String hget(String key, String hkey) {
        return (String) redisTemplate.opsForHash().get(key, hkey);
    }

    /**
     * hash的赋值去del
     *
     * @param key  key
     * @param hkey hkey
     */
    public Long hdel(String key, String hkey) {
        return (Long) redisTemplate.opsForHash().delete(key, hkey);
    }


    /**
     * Map集合的赋值去取
     */
    public void hPutAll(String key, Map<String, String> value) {

        String hkey = "CREATE_TIME";
        String hvalue = String.valueOf(System.currentTimeMillis());
        redisTemplate.opsForHash().put(key, hkey, hvalue);
        Iterator<Map.Entry<String, String>> i = value.entrySet().iterator();

        while (i.hasNext()) {
            Map.Entry<String, String> next = i.next();
            hkey = next.getKey();
            hvalue = next.getValue();
            redisTemplate.opsForHash().put(key, hkey, hvalue);

        }
    }

    /**
     * Map集合的赋值去取
     */
    public Map<String, String> hGetAll(String key) {
        Map<String, String> map = redisTemplate.opsForHash().entries(key);
        return map;
    }


    /**
     * List集合的赋值去取
     */
    public void setLists(String key, List list) {
        redisTemplate.opsForList().leftPush(key, list);
    }

    public String getListStartEnd(String key, int start, int end) {
        String result = new Gson().toJson(redisTemplate.opsForList().range(key, start, end));
        return result.substring(1, result.length() - 1);
    }

    /**
     * 查询key的剩余存活时间
     */
    public long getKeyExpireTime(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 设置key的剩余存活时间
     */
    public boolean setKeyExpireTime(String key, int timeOut) {
        long times = 0;
        if (timeOut > 0) {
            times = timeOut * 60;
        } else {
            times = DefaultTime;
        }
        return redisTemplate.expire(key, times, TimeUnit.SECONDS);
    }

    public boolean existK(String key) {
        if (redisTemplate.hasKey(key)) {
            return true;
        }
        return false;
    }

    public boolean existSetK(String key) {
        if (redisTemplate.opsForSet().isMember(key, null)) {
            return true;
        }
        return false;
    }

    /**
     * 判断key是否存在
     */
    public boolean exitsKey(String key) {
        Object obj = redisTemplate.execute(new RedisCallback() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.exists(key.getBytes());
            }
        });
        boolean flag = true;
        if (obj.toString().equals("false")) {
            return false;
        }
        return flag;
    }

    public Object evalSha(String sha1, List<String> keys) {

        Object obj = redisTemplate.execute(new RedisCallback() {
            public Object doInRedis(RedisConnection connection) throws DataAccessException {

                RedisSerializer serializer = redisTemplate.getStringSerializer();

                List<byte[]> byteList = keys.stream().map(s -> serializer.serialize(s)).collect(Collectors.toList());

                byte[][] array = byteList.toArray(new byte[byteList.size()][]);

                Object ret = connection.evalSha(sha1, ReturnType.VALUE, keys.size(), array);
                return ret;
            }
        });
        return obj;
    }


    /**
     * 根据key获取对象
     *
     * @param key the key
     * @return the string
     */
    public Object getObject(final String key) {
        Object resultStr = redisTemplate.execute((RedisCallback<Object>) connection ->
        {
            RedisSerializer<String> serializer = keySerializer();
            byte[] keys = serializer.serialize(key);
            byte[] values = connection.get(keys);
            return OBJECT_SERIALIZER.deserialize(values);
        });
        log.debug("[redisTemplate redis]取出 缓存  url:{} ", key);
        return resultStr;
    }


    /**
     * 添加到带有 过期时间的  缓存
     *
     * @param key   redis主键
     * @param value 值
     * @param time  过期时间(单位秒)
     */
    public void setExpire(String key, String value, int time) {


        redisTemplate.execute((RedisCallback<Long>) connection ->
        {
            RedisSerializer<String> serializer = keySerializer();
            byte[] keys = serializer.serialize(key);
            byte[] values = valueSerializer().serialize(value);
            connection.setEx(keys, time, values);
            return 1L;
        });
    }

    /**
     * 添加到带有 无期时间的  缓存
     *
     * @param key   redis主键
     * @param value 值
     */
    public void setObject(final String key, final Object value) {
        redisTemplate.execute((RedisCallback<Long>) connection ->
        {
            RedisSerializer<String> serializer = getRedisSerializer();
            byte[] keys = serializer.serialize(key);
            byte[] values = OBJECT_SERIALIZER.serialize(value);
            connection.set(keys, values);
            return 1L;
        });
    }

    /**
     * 添加到带有 过期时间的  缓存
     *
     * @param key   redis主键
     * @param value 值
     * @param time  过期时间(单位秒)
     */
    public void setExpire(final String key, final Object value, final long time) {
        redisTemplate.execute((RedisCallback<Long>) connection ->
        {
            RedisSerializer<String> serializer = getRedisSerializer();
            byte[] keys = serializer.serialize(key);
            byte[] values = OBJECT_SERIALIZER.serialize(value);
            connection.setEx(keys, time, values);
            return 1L;
        });
    }

    /**
     * 一次性添加数组到   过期时间的  缓存，不用多次连接，节省开销
     *
     * @param keys   the keys
     * @param values the values
     */
    public void set(final String[] keys, final Object[] values) {
        redisTemplate.execute((RedisCallback<Long>) connection ->
        {
            RedisSerializer<String> serializer = getRedisSerializer();
            for (int i = 0; i < keys.length; i++) {
                byte[] bKeys = serializer.serialize(keys[i]);
                byte[] bValues = OBJECT_SERIALIZER.serialize(values[i]);
                connection.set(bKeys, bValues);
            }
            return 1L;
        });
    }


    /**
     * 获取 RedisSerializer
     *
     * @return the redis serializer
     */
    protected RedisSerializer<String> keySerializer() {
        return redisTemplate.getKeySerializer();
    }

    /**
     * 获取 RedisSerializer
     *
     * @return the redis serializer
     */
    protected RedisSerializer<String> valueSerializer() {
        return redisTemplate.getValueSerializer();
    }

    /**
     * 获取 RedisSerializer
     *
     * @return the redis serializer
     */
    protected RedisSerializer<String> getRedisSerializer() {
        return redisTemplate.getStringSerializer();
    }

    public <K, T> T executeScript(RedisScript<T> script, List<K> keys, Object... args) {
        Object r = redisTemplate.execute(script, keys, args);
        return (T) r;
    }

    public String loadScript(RedisScript script) {
        RedisConnection redisConnection = redisTemplate.getConnectionFactory().getConnection();
        return redisConnection.scriptLoad(script.getScriptAsString().getBytes(Charset.forName("UTF-8")));
    }

    public String getSessionId(String uid) {
        return hget("SESSION:ID:" + SessionHolder.getSessionIDStore(), uid);
    }

    public void setSessionId(String uid, String sid) {
        hset("SESSION:ID:" + SessionHolder.getSessionIDStore(), uid, sid);
    }

    public void delAll(Set<String> keys) {

    }
}
