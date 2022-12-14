--- 此脚本的环境： redis 内部，不是运行在 nginx 内部

---方法：申请令
---eg
----- /usr/local/redis/bin/redis-cli -a 123456  evalsha   "75e0f0c8ab378aa178c3d7fe2aeabc4fc0e289fa" 1 "rate_limiter:seckill:1"  acquire 1
---
--- -1 failed
--- 1 success
--- @param key key 限流关键字
--- @param apply  申请的令牌数量
local function acquire(key, apply)
    local times = redis.call('TIME');
    -- times[1] 秒数   -- times[2] 微秒数
    local curr_mill_second = times[1] * 1000000 + times[2];
    curr_mill_second = curr_mill_second / 1000;

    local cacheInfo = redis.pcall("HMGET", key, "last_mill_second", "curr_permits", "max_permits", "rate")
    --- 局部变量：上次申请的时间
    local last_mill_second = cacheInfo[1];
    --- 局部变量：之前的令牌数
    local curr_permits = tonumber(cacheInfo[2]);
    --- 局部变量：桶的容量
    local max_permits = tonumber(cacheInfo[3]);
    --- 局部变量：令牌的发放速率
    local rate = tonumber(cacheInfo[4]);
    --- 局部变量：本次的令牌数
    local local_curr_permits = 0;

    if (type(last_mill_second) ~= 'boolean' and last_mill_second ~= nil) then
        -- 计算时间段内的令牌数
        local reverse_permits = math.floor(((curr_mill_second - last_mill_second) / 1000) * rate);
        -- 令牌总数
        local expect_curr_permits = reverse_permits + curr_permits;
        -- 可以申请的令牌总数
        local_curr_permits = math.min(expect_curr_permits, max_permits);
    else
        -- 第一次获取令牌
        redis.pcall("HSET", key, "last_mill_second", curr_mill_second)
        local_curr_permits = max_permits;
    end

    local result = -1;
    -- 有足够的令牌可以申请
    if (local_curr_permits - apply >= 0) then
        -- 保存剩余的令牌
        redis.pcall("HSET", key, "curr_permits", local_curr_permits - apply);
        -- 为下次的令牌获取，保存时间
        redis.pcall("HSET", key, "last_mill_second", curr_mill_second)
        -- 返回令牌获取成功
        result = 1;
    else
        -- 返回令牌获取失败
        result = -1;
    end
    return result
end
--eg
-- /usr/local/redis/bin/redis-cli  -a 123456  --eval   /vagrant/LuaDemoProject/src/luaScript/redis/rate_limiter.lua key , acquire 1  1

-- 获取 sha编码的命令
-- /usr/local/redis/bin/redis-cli  -a 123456  script load "$(cat  /vagrant/LuaDemoProject/src/luaScript/redis/rate_limiter.lua)"
-- /usr/local/redis/bin/redis-cli  -a 123456  script exists  "75e0f0c8ab378aa178c3d7fe2aeabc4fc0e289fa"

-- /usr/local/redis/bin/redis-cli -a 123456  evalsha   "75e0f0c8ab378aa178c3d7fe2aeabc4fc0e289fa" 1 "rate_limiter:seckill:2"  init 2  1

--local rateLimiterSha = "e4e49e4c7b23f0bf7a2bfee73e8a01629e33324b";

---方法：初始化限流 Key
--- 1 success
--- @param key key
--- @param max_permits  桶的容量
--- @param rate  令牌的发放速率
local function init(key, max_permits, rate)
    ---     local rate_limit_info = redis.pcall("HMGET", key, "last_mill_second", "curr_permits", "max_permits", "rate")
    redis.pcall("HMSET", key, "max_permits", max_permits, "rate", rate, "curr_permits", max_permits)
    return 1;
end
--eg
-- /usr/local/redis/bin/redis-cli -a 123456 --eval   /vagrant/LuaDemoProject/src/luaScript/redis/rate_limiter.lua key , init 1  1
-- /usr/local/redis/bin/redis-cli -a 123456 --eval   /vagrant/LuaDemoProject/src/luaScript/redis/rate_limiter.lua  "rate_limiter:seckill:1"  , init 1  1


---方法：删除限流 Key
local function delete(key)
    redis.pcall("DEL", key)
    return 1;
end
--eg
-- /usr/local/redis/bin/redis-cli  --eval   /vagrant/LuaDemoProject/src/luaScript/redis/rate_limiter.lua key , delete


local key = KEYS[1]
local method = ARGV[1]
if method == 'acquire' then
    return acquire(key, ARGV[2])
elseif method == 'init' then
    return init(key, ARGV[2], ARGV[3])
elseif method == 'delete' then
    return delete(key)
else
    --ignore
end
