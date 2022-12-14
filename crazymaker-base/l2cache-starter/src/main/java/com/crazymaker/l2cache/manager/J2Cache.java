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
package com.crazymaker.l2cache.manager;

import com.crazymaker.l2cache.config.J2CacheCoreConfig;

import java.io.IOException;

/**
 * J2Cache 的缓存入口
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class J2Cache {

    private final static String CONFIG_FILE = "/j2cache.properties";

    private final static J2CacheBuilder builder;

    static {
        try {
            J2CacheCoreConfig config = J2CacheCoreConfig.initFromConfig(CONFIG_FILE);
            builder = J2CacheBuilder.init(config);
        } catch (IOException e) {
            throw new CacheException("Failed to load j2cache configuration " + CONFIG_FILE, e);
        }
    }

    /**
     * 返回缓存操作接口
     *
     * @return CacheChannel
     */
    public static CacheChannel getChannel() {
        return builder.getChannel();
    }

    /**
     * 关闭 J2Cache
     */
    public static void close() {
        builder.close();
    }
}
