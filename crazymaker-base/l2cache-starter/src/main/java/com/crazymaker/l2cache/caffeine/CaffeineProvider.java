/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crazymaker.l2cache.caffeine;

import com.crazymaker.l2cache.manager.Cache;
import com.crazymaker.l2cache.manager.CacheChannel;
import com.crazymaker.l2cache.manager.CacheException;
import com.crazymaker.l2cache.manager.CacheExpiredListener;
import com.crazymaker.l2cache.manager.CacheObject;
import com.crazymaker.l2cache.manager.CacheProvider;
import com.crazymaker.springcloud.common.util.AntPathMatcher;
import com.crazymaker.springcloud.common.util.PatternMatcher;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine cache provider
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class CaffeineProvider implements CacheProvider {

    private final static Logger log = LoggerFactory.getLogger(CaffeineProvider.class);

    private final static String PREFIX_REGION = "region.";
    private final static String DEFAULT_REGION = "default";
    private ConcurrentHashMap<String, CaffeineCache> caches = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, CacheConfig> cacheConfigs = new ConcurrentHashMap<>();
    private PatternMatcher patternMatcher = new AntPathMatcher();

    @Override
    public String name() {
        return "caffeine";
    }

    @Override
    public int level() {
        return CacheObject.LEVEL_1;
    }

    @Override
    public Collection<CacheChannel.Region> regions() {
        Collection<CacheChannel.Region> regions = new ArrayList<>();
        caches.forEach((k, c) -> regions.add(new CacheChannel.Region(k, c.size(), c.ttl())));
        return regions;
    }

    @Override
    public Cache buildCache(String region, CacheExpiredListener listener) {
        return caches.computeIfAbsent(region, v -> {
            CacheConfig config = findCacheConfig(region);
            if (config == null) {
                log.warn("Caffeine cache [{}] not defined, using default.", region);
                config = cacheConfigs.get(DEFAULT_REGION);
                if (config == null)
                    throw new CacheException(String.format("Undefined [default] caffeine cache"));
            }
            return newCaffeineCache(region, config.size, config.expire, listener);
        });
    }

    @Override
    public Cache buildCache(String region, long timeToLiveInSeconds, CacheExpiredListener listener) {
        CaffeineCache cache = caches.computeIfAbsent(region, v -> {
            CacheConfig config = findCacheConfig(region);
            if (config != null && config.expire != timeToLiveInSeconds)
                throw new IllegalArgumentException(String.format("Region [%s] TTL %d not match with %d", region, config.expire, timeToLiveInSeconds));

            if (config == null) {
                config = cacheConfigs.get(DEFAULT_REGION);
                if (config == null)
                    throw new CacheException(String.format("Undefined caffeine cache region name = %s", region));
            }

            log.info("Started caffeine region [{}] with TTL: {}", region, timeToLiveInSeconds);
            return newCaffeineCache(region, config.size, timeToLiveInSeconds, listener);
        });

        if (cache != null && cache.ttl() != timeToLiveInSeconds)
            throw new IllegalArgumentException(String.format("Region [%s] TTL %d not match with %d", region, cache.ttl(), timeToLiveInSeconds));

        return cache;
    }

    @Override
    public void removeCache(String region) {
        cacheConfigs.remove(region);
        caches.remove(region);
    }

    /**
     * 返回对 Caffeine cache 的 封装
     *
     * @param region   region name
     * @param size     max cache object size in memory
     * @param expire   cache object expire time in second
     *                 if this parameter set to 0s or negative numbers
     *                 means never expire
     * @param listener j2cache cache listener
     * @return CaffeineCache
     */
    private CaffeineCache newCaffeineCache(String region, long size, long expire, CacheExpiredListener listener) {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
        caffeine = caffeine.maximumSize(size)
                .removalListener((k, v, cause) -> {
                    /*
                     * 程序删除的缓存不做通知处理，因为上层已经做了处理
                     * 当缓存数据不是因为手工删除和超出容量限制而被删除的情况，就需要通知上层侦听器
                     */
                    if (cause != RemovalCause.EXPLICIT && cause != RemovalCause.REPLACED && cause != RemovalCause.SIZE)
                        listener.notifyElementExpired(region, (String) k);
                });
        if (expire > 0) {
            caffeine = caffeine.expireAfterWrite(expire, TimeUnit.SECONDS);
        }
        com.github.benmanes.caffeine.cache.Cache<String, Object> loadingCache = caffeine.build();
        return new CaffeineCache(loadingCache, size, expire);
    }

    /**
     * <p>配置示例</p>
     * <ul>
     * <li>caffeine.region.default = 10000,1h</li>
     * <li>caffeine.region.Users = 10000,1h</li>
     * <li>caffeine.region.Blogs = 80000,30m</li>
     * </ul>
     *
     * @param props current configuration settings.
     */
    @Override
    public void start(Properties props) {
        for (String region : props.stringPropertyNames()) {
            if (!region.startsWith(PREFIX_REGION))
                continue;
            String s_config = props.getProperty(region).trim();
            region = region.substring(PREFIX_REGION.length());
            this.saveCacheConfig(region, s_config);
        }
        //加载 Caffeine 独立配置文件
        String propertiesFile = props.getProperty("properties");
        if (propertiesFile != null && propertiesFile.trim().length() > 0) {
            InputStream stream = null;
            try {
                stream = getClass().getResourceAsStream(propertiesFile);
                if (stream == null) {
                    stream = getClass().getClassLoader().getResourceAsStream(propertiesFile);
                }
                Properties regionsProps = new Properties();
                regionsProps.load(stream);
                for (String region : regionsProps.stringPropertyNames()) {
                    String s_config = regionsProps.getProperty(region).trim();
                    this.saveCacheConfig(region, s_config);
                }
            } catch (IOException e) {
                log.error("Failed to load caffeine regions define {}", propertiesFile, e);
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private CacheConfig findCacheConfig(String region) {
        for (Map.Entry<String, CacheConfig> entry : cacheConfigs.entrySet()) {
            if (patternMatcher.matches(entry.getKey(), region)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void saveCacheConfig(String region, String region_config) {
        CacheConfig cfg = CacheConfig.parse(region_config);
        if (cfg == null)
            log.warn("Illegal caffeine cache config [{}={}]", region, region_config);
        else
            cacheConfigs.put(region, cfg);
    }

    @Override
    public void stop() {
        caches.clear();
        cacheConfigs.clear();
    }

    /**
     * 缓存配置
     */
    private static class CacheConfig {

        private long size = 0L;
        private long expire = 0L;

        public static CacheConfig parse(String cfg) {
            CacheConfig cacheConfig = null;
            String[] cfgs = cfg.split(",");
            if (cfgs.length == 1) {
                cacheConfig = new CacheConfig();
                String sSize = cfgs[0].trim();
                cacheConfig.size = Long.parseLong(sSize);
            } else if (cfgs.length == 2) {
                cacheConfig = new CacheConfig();
                String sSize = cfgs[0].trim();
                String sExpire = cfgs[1].trim();
                cacheConfig.size = Long.parseLong(sSize);
                char unit = Character.toLowerCase(sExpire.charAt(sExpire.length() - 1));
                cacheConfig.expire = Long.parseLong(sExpire.substring(0, sExpire.length() - 1));
                switch (unit) {
                    case 's'://seconds
                        break;
                    case 'm'://minutes
                        cacheConfig.expire *= 60;
                        break;
                    case 'h'://hours
                        cacheConfig.expire *= 3600;
                        break;
                    case 'd'://days
                        cacheConfig.expire *= 86400;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown expire unit:" + unit);
                }
            }
            return cacheConfig;
        }

        @Override
        public String toString() {
            return String.format("[SIZE:%d,EXPIRE:%d]", size, expire);
        }

    }

}
