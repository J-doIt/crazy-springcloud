--返回值说明
--1 排队成功
--2 排队商品没有找到
--3 人数超过限制
--4 库存不足
--5 排队过了
--6 秒杀过了
-- -2 Lua 方法不存在
local function setToken(skuId, userId, token)

    --检查token 是否存在
    local oldToken = redis.call("hget", "seckill:queue:" .. skuId, userId);
    if oldToken then
        return 5; --5 排队过了
    end


    --获取商品缓存次数
    local skuJson = redis.call("get", "seckill:skus:" .. skuId);
    if not skuJson then
        --redis.debug("秒杀商品没有找到")
        return 2;  --2 秒杀商品没有找到
    end
    --redis.log(redis.LOG_NOTICE, skuJson)
    local skuDto = cjson.decode(skuJson);
    --redis.log(redis.LOG_NOTICE, "sku title=" .. skuDto.title)
    local stockCount = tonumber(skuDto.stockCount);
    --redis.log(redis.LOG_NOTICE, "stockCount=" .. stockCount)
    if stockCount <= 0 then
        return 4;  --4 库存不足
    end

    stockCount = stockCount - 1;
    skuDto.stockCount = stockCount;

    redis.call("set", "seckill:skus:" .. skuId, cjson.encode(skuDto));
    redis.call("hset", "seckill:queue:" .. skuId, userId, token);
    return 1; --1 排队成功

end
--eg
-- /usr/local/redis/bin/redis-cli  --eval   /work/develop/LuaDemoProject/src/luaScript/module/seckill/seckill.lua setToken  , 1  1  1


--返回值说明
--5 排队过了
-- -1 没有排队
local function checkToken(skuId, userId, token)
    --检查token 是否存在
    local oldToken = redis.call("hget", "seckill:queue:" .. skuId, userId);
    if oldToken and (token == oldToken) then
        --return 1 ;
        return 5; --5 排队过了
    end
    return -1; -- -1 没有排队
end

--eg
-- /usr/local/redis/bin/redis-cli  --eval   /work/develop/LuaDemoProject/src/luaScript/module/seckill/seckill.lua checkToken  , 1  1  fca9b425-ac48-4c44-9e99-92d18898873c



local function deleteToken(skuId, userId)
    redis.call("hdel", "seckill:queue:" .. skuId, userId);
    return 1;
end
--eg
--  /usr/local/redis/bin/redis-cli  --eval   /work/develop/LuaDemoProject/src/luaScript/module/seckill/seckill.lua deleteToken  , 1  1



local method = KEYS[1]

local skuId = ARGV[1]
local userId = ARGV[2]
local token = ARGV[3]

if method == 'setToken' then
    return setToken(skuId, userId, token)
elseif method == 'checkToken' then
    return checkToken(skuId, userId, token)
elseif method == 'deleteToken' then
    return deleteToken(skuId, userId)
else
    return -2; -- Lua方法不存在
end

