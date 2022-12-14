--返回值说明
--1 排队成功
--2 秒杀库存没有找到
--3 人数超过限制
--4 库存不足
--5 排队过了
--6 秒杀过了
-- -2 Lua 方法不存在

--eg
-- /usr/local/redis/bin/redis-cli -a  123456 --eval   /vagrant/LuaDemoProject/src/luaScript/module/seckill/seckill.lua setToken  , 4b70903f6e1aa87788d3ea962f8b2f0e  38  demotoken

local function setToken(exposedKey, userId, token)

    --检查token 是否存在
    local oldToken = redis.call("hget", "seckill:queue:" .. exposedKey, userId);
    if oldToken then
        return 5; --5 排队过了
    end


    --获取商品缓存次数
    local stock = redis.call("get", "seckill:stock:" .. exposedKey);
    --local stock = redis.call("get", "seckill:stock:4b70903f6e1aa87788d3ea962f8b2f0e" );
    -- redis.debug("stock="..tostring(stock))
    if stock then

        local stockCount = tonumber(stock);
        --redis.log(redis.LOG_NOTICE, "stock=" .. stock)
        if stockCount <= 0 then
            return 4;  --4 库存不足
        end

        -- stockCount = stockCount - 1;
        -- redis.call("set", "seckill:stock:" .. exposedKey,stockCount);

        redis.call("decr", "seckill:stock:" .. exposedKey);
        redis.call("hset", "seckill:queue:" .. exposedKey, userId, token);
        return 1; --1 排队成功
    else
        --redis.debug("秒杀库存没有找到")
        return 2;  --2 秒杀库存没有找到
    end
end


--eg
-- /usr/local/redis/bin/redis-cli -a  123456 --eval   /vagrant/LuaDemoProject/src/luaScript/module/seckill/seckill.lua setToken  , 4b70903f6e1aa87788d3ea962f8b2f0e 38  demotoken

--返回值说明
--5 排队过了
-- -1 没有排队
local function checkToken(exposedKey, userId, token)
    --检查token 是否存在
    local oldToken = redis.call("hget", "seckill:queue:" .. exposedKey, userId);
    if oldToken and (token == oldToken) then
        --return 1 ;
        return 5; --5 排队过了
    end
    return -1; -- -1 没有排队
end

--eg
-- /usr/local/redis/bin/redis-cli  --eval   /work/develop/LuaDemoProject/src/luaScript/module/seckill/seckill.lua checkToken  , 1  1  fca9b425-ac48-4c44-9e99-92d18898873c



local function deleteToken(exposedKey, userId)
    redis.call("hdel", "seckill:queue:" .. exposedKey, userId);
    return 1;
end
--eg
--  /usr/local/redis/bin/redis-cli  --eval   /work/develop/LuaDemoProject/src/luaScript/module/seckill/seckill.lua deleteToken  , 1  1



local method = KEYS[1]

local exposedKey = ARGV[1]
local userId = ARGV[2]
local token = ARGV[3]

if method == 'setToken' then
    return setToken(exposedKey, userId, token)
elseif method == 'checkToken' then
    return checkToken(exposedKey, userId, token)
elseif method == 'deleteToken' then
    return deleteToken(exposedKey, userId)
else
    return -2; -- Lua方法不存在
end

