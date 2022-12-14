package com.crazymaker.springcloud.standard.lua;

import com.crazymaker.springcloud.common.util.IOUtil;
import com.crazymaker.springcloud.standard.lock.RedisLockService;
import com.crazymaker.springcloud.standard.ratelimit.RedisRateLimitImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

@Slf4j
public class ScriptHolder {

    /**
     * 秒杀令牌操作的脚本
     */
    static String seckillLua = "script/seckill.lua";
    public static final String SECKILL_LUA_SHA_1 = "seckill:lua:sha1";

    public static RedisScript<Long> seckillScript = null;

    public static synchronized RedisScript<Long> getSeckillScript() {

        if (null == seckillScript) {
            String script = IOUtil.loadJarFile(RedisLockService.class.getClassLoader(), seckillLua);
            seckillScript = new DefaultRedisScript<>(script, Long.class);

        }
        return seckillScript;
    }


    static String lockLua = "script/lock.lua";
    static RedisScript<Long> lockScript = null;

    public static synchronized RedisScript<Long> getLockScript() {

        if (null == lockScript) {
            String script = IOUtil.loadJarFile(RedisLockService.class.getClassLoader(), lockLua);
            lockScript = new DefaultRedisScript<>(script, Long.class);

        }
        return lockScript;
    }

    static String unLockLua = "script/unlock.lua";
    static RedisScript<Long> unLockScript = null;

    public static synchronized RedisScript<Long> getUnlockScript() {

        if (null == unLockScript) {
            String script = IOUtil.loadJarFile(RedisLockService.class.getClassLoader(), unLockLua);
            unLockScript = new DefaultRedisScript<>(script, Long.class);

        }
        return unLockScript;
    }

    //lua 脚本的类路径
    private static String rateLimitLua = "script/rate_limiter.lua";
    private static RedisScript<Long> rateLimiterScript = null;

    public static synchronized RedisScript<Long> getRateLimitScript() {
        if (null == rateLimiterScript) {
            //从类路径文件中，加载 lua 脚本

            String script = IOUtil.loadJarFile(RedisRateLimitImpl.class.getClassLoader(), rateLimitLua);

            if (StringUtils.isEmpty(script)) {
                log.error("lua script load failed:" + rateLimitLua);

            } else {
                //创建 lua 脚本实例
                rateLimiterScript = new DefaultRedisScript<>(script, Long.class);
            }
        }
        return rateLimiterScript;
    }

}
