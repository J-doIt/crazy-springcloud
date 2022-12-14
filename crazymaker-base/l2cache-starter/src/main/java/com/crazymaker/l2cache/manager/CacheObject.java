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

/**
 * Cached object description
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class CacheObject {

    public final static byte LEVEL_1 = 1;    //一级缓存数据
    public final static byte LEVEL_2 = 2;    //二级缓存数据
    public final static byte LEVEL_OUTER = 3;    //外部数据

    private String region;
    private String key;
    private Object value;
    private byte level;

    public CacheObject(String region, String key, byte level) {
        this(region, key, level, null);
    }

    public CacheObject(String region, String key, byte level, Object value) {
        this.region = region;
        this.key = key;
        this.level = level;
        this.value = value;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * 获取数据所在的缓存区域
     *
     * @return cache region name
     */
    public String getRegion() {
        return region;
    }

    /**
     * 缓存数据键值
     *
     * @return cache key
     */
    public String getKey() {
        return key;
    }

    /**
     * 缓存对象
     *
     * @return cache object include null object
     */
    public Object getValue() {
        if (value == null || value.getClass().equals(NullObject.class) || value.getClass().equals(Object.class))
            return null;
        return value;
    }

    /**
     * 返回实际缓存的对象
     *
     * @return cache raw object
     */
    public Object rawValue() {
        return value;
    }

    /**
     * 缓存所在的层级
     *
     * @return cache level
     */
    public byte getLevel() {
        return level;
    }

    public String asString() {
        return String.valueOf(value);
    }

    public int asInt() {
        return (value instanceof String) ? Integer.parseInt((String) value) : (Integer) value;
    }

    public int asInt(int defValue) {
        try {
            return Integer.parseInt(asString());
        } catch (Exception e) {
            return defValue;
        }
    }

    public double asDouble() {
        return (value instanceof String) ? Double.parseDouble((String) value) : (Double) value;
    }

    public double asDouble(double defValue) {
        try {
            return Double.parseDouble(asString());
        } catch (Exception e) {
            return defValue;
        }
    }

    public long asLong() {
        return (value instanceof String) ? Long.parseLong((String) value) : (Long) value;
    }

    public long asLong(long defValue) {
        try {
            return Long.parseLong(asString());
        } catch (Exception e) {
            return defValue;
        }
    }

    public float asFloat() {
        return (value instanceof String) ? Float.parseFloat((String) value) : (Float) value;
    }

    public float asFloat(float defValue) {
        try {
            return Float.parseFloat(asString());
        } catch (Exception e) {
            return defValue;
        }
    }

    @Override
    public String toString() {
        return String.format("[%s,%s,L%d]=>%s", region, key, level, getValue());
    }

}
