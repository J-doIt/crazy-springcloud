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
package com.crazymaker.l2cache.cluster;

import com.crazymaker.l2cache.manager.CacheException;
import com.crazymaker.l2cache.manager.CacheProviderHolder;

import java.util.Properties;

/**
 * 集群策略工厂
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class ClusterPolicyFactory {

    private ClusterPolicyFactory() {
    }

    /**
     * 初始化集群消息通知机制
     *
     * @param holder    CacheProviderHolder instance
     * @param broadcast j2cache.broadcast value
     * @param props     broadcast configuations
     * @return ClusterPolicy instance
     */
    public final static ClusterPolicy init(CacheProviderHolder holder, String broadcast, Properties props) {

        ClusterPolicy policy = null;
        if ("redis".equalsIgnoreCase(broadcast)) {
//            policy = ClusterPolicyFactory.redis(props, holder);
            policy = new NoneClusterPolicy();

        } else if ("jgroups".equalsIgnoreCase(broadcast)) {
//              policy = ClusterPolicyFactory.jgroups(props, holder);
            policy = new NoneClusterPolicy();

        } else if ("rabbitmq".equalsIgnoreCase(broadcast)) {
//         policy = ClusterPolicyFactory.rabbitmq(props, holder);
            policy = new NoneClusterPolicy();

        } else if ("rocketmq".equalsIgnoreCase(broadcast)) {
            policy = ClusterPolicyFactory.rocketmq(props, holder);
        } else if ("lettuce".equalsIgnoreCase(broadcast)) {
//         policy = ClusterPolicyFactory.lettuce(props, holder);
            policy = new NoneClusterPolicy();

        } else if ("none".equalsIgnoreCase(broadcast)) {
            policy = new NoneClusterPolicy();
        } else {
            policy = ClusterPolicyFactory.custom(broadcast, props, holder);
        }
        return policy;
    }


    private final static ClusterPolicy rocketmq(Properties props, CacheProviderHolder holder) {
        RocketMQClusterPolicy policy = new RocketMQClusterPolicy(props);
        policy.connect(props, holder);
        return policy;
    }

    /**
     * 加载自定义的集群通知策略
     *
     * @param classname
     * @param props
     * @return
     */
    private final static ClusterPolicy custom(String classname, Properties props, CacheProviderHolder holder) {
        try {
            ClusterPolicy policy = (ClusterPolicy) Class.forName(classname).newInstance();
            policy.connect(props, holder);
            return policy;
        } catch (Exception e) {
            throw new CacheException("Failed in load custom cluster policy. class = " + classname, e);
        }
    }

}
