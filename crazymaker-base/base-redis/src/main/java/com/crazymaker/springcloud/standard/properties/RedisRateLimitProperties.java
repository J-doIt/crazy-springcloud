package com.crazymaker.springcloud.standard.properties;

import com.crazymaker.springcloud.standard.ratelimit.RedisRateLimitImpl;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "application.rate.limit.redis" )
public class RedisRateLimitProperties
{
    private List<RedisRateLimitImpl.LimiterInfo> limiterInfos;



}
