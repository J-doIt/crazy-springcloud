/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crazymaker.springcloud.kafka.mq.admin;

import com.crazymaker.springcloud.common.exception.BusinessException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.crazymaker.springcloud.common.util.BeanUtil.asList;


/**
 * An admin that delegates to an {@link AdminClient} to create topics defined
 * in the application context.
 *
 * @author Gary Russell
 * @since 1.3
 */
public class KafkaAdmin implements ApplicationContextAware {

    private static final int DEFAULT_CLOSE_TIMEOUT = 10;

    private static final int DEFAULT_OPERATION_TIMEOUT = 30;

    private final static Log logger = LogFactory.getLog(KafkaAdmin.class);

    private final Map<String, Object> config;

    private ApplicationContext applicationContext;

    private int closeTimeout = DEFAULT_CLOSE_TIMEOUT;

    private int operationTimeout = DEFAULT_OPERATION_TIMEOUT;

    private boolean fatalIfBrokerNotAvailable;


    private boolean initializingContext;

    /**
     * Create an instance with an {@link AdminClient} based on the supplied
     * configuration.
     *
     * @param config the configuration for the {@link AdminClient}.
     */
    public KafkaAdmin(Map<String, Object> config) {
        this.config = new HashMap<>(config);
        refreshAdmin();
    }

    private void refreshAdmin() {

        if (null != adminClient) {
            adminClient.close();
        }
        try {
            adminClient = AdminClient.create(this.config);
        } catch (Exception e) {
            if (!this.initializingContext || this.fatalIfBrokerNotAvailable) {
                throw new IllegalStateException("Could not create admin", e);
            }
        }
    }

    /**
     * Set the close timeout in seconds. Defaults to 10 seconds.
     *
     * @param closeTimeout the timeout.
     */
    public void setCloseTimeout(int closeTimeout) {
        this.closeTimeout = closeTimeout;
    }

    /**
     * Set the operation timeout in seconds. Defaults to 30 seconds.
     *
     * @param operationTimeout the timeout.
     */
    public void setOperationTimeout(int operationTimeout) {
        this.operationTimeout = operationTimeout;
    }

    /**
     * Set to true if you want the application context to fail to load if we are unable
     * to connect to the broker during initialization, to check/add topics.
     *
     * @param fatalIfBrokerNotAvailable true to fail.
     */
    public void setFatalIfBrokerNotAvailable(boolean fatalIfBrokerNotAvailable) {
        this.fatalIfBrokerNotAvailable = fatalIfBrokerNotAvailable;
    }


    /**
     * Get an unmodifiable copy of this admin's configuration.
     *
     * @return the configuration map.
     */
    public Map<String, Object> getConfig() {
        return Collections.unmodifiableMap(this.config);
    }

    AdminClient adminClient = null;

    /**
     * Call this method to check/add topics; this might be needed if the broker was not
     */
    public final boolean add(Collection<NewTopic> newTopics) {
        refreshAdmin();
        if (newTopics.size() > 0) {

            if (adminClient != null) {
                try {
                    addTopicsIfNeeded(newTopics);
                    return true;
                } catch (Throwable e) {
                    if (e instanceof Error) {
                        throw (Error) e;
                    }
                    if (!this.initializingContext || this.fatalIfBrokerNotAvailable) {
                        throw new IllegalStateException("Could not configure topics", e);
                    }
                } finally {
                    this.initializingContext = false;
                    adminClient.close(this.closeTimeout, TimeUnit.SECONDS);
                }
            }
        }
        this.initializingContext = false;
        return false;
    }

    private void addTopicsIfNeeded(Collection<NewTopic> topics) throws Throwable {
        refreshAdmin();
        if (topics.size() > 0) {
            Map<String, NewTopic> topicNameToTopic = new HashMap<>();
            topics.forEach(t -> topicNameToTopic.compute(t.name(), (k, v) -> v = t));
            DescribeTopicsResult topicInfo = adminClient
                    .describeTopics(topics.stream()
                            .map(NewTopic::name)
                            .collect(Collectors.toList()));
            List<NewTopic> topicsToAdd = new ArrayList<>();
            topicInfo.values().forEach((n, f) ->
            {
                try {
                    TopicDescription topicDescription = f.get(this.operationTimeout, TimeUnit.SECONDS);
                    if (topicNameToTopic.get(n).numPartitions() != topicDescription.partitions().size()) {
                        if (logger.isInfoEnabled()) {
                            logger.info(String.format(
                                    "Topic '%s' exists but has a different partition count: %d not %d", n,
                                    topicDescription.partitions().size(), topicNameToTopic.get(n).numPartitions()));
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (TimeoutException e) {
                    throw new BusinessException("Timed out waiting to get existing topics");
                } catch (ExecutionException e) {
                    topicsToAdd.add(topicNameToTopic.get(n));
                }
            });
            if (topicsToAdd.size() > 0) {
                CreateTopicsResult topicResults = adminClient.createTopics(topicsToAdd);
                try {
                    topicResults.all().get(this.operationTimeout, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Interrupted while waiting for topic creation results", e);
                } catch (TimeoutException e) {
                    throw new BusinessException("Timed out waiting for create topics results");
                } catch (ExecutionException e) {
                    logger.error("Failed to create topics", e.getCause());
                    throw new BusinessException("Failed to create topics");
                }
            }
        }
    }

    /**
     * Set the ApplicationContext that this object runs in.
     * Normally this call will be used to initialize the object.
     * <p>Invoked after population of normal bean properties but before an init callback such
     * as {@link InitializingBean#afterPropertiesSet()}
     *
     * @throws BeansException if thrown by application context methods
     * @see BeanInitializationException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    /**
     * 列出所有kakfa topic 信息 默认不会列出kafka内部的topic 超时时间默认
     */
    public Set<String> listTopics() {
        refreshAdmin();
        ListTopicsResult listTopic = this.adminClient.listTopics();
        try {
            return listTopic.names().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new BusinessException("查看 topic list 失败");

        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new BusinessException("查看 topic list 失败");
        }
    }

    /**
     * check topic 是否存在
     */
    private Map<String, Boolean> checkExists(Set<String> topicSames)
            throws InterruptedException, ExecutionException {
        refreshAdmin();
        Map<String, Boolean> result = new HashMap<String, Boolean>(1 << 4);
        Set<String> kafkaTopicSet = new HashSet<String>(1 << 4);

        kafkaTopicSet.addAll(this.listTopics());

        for (String topicName : topicSames) {
            result.put(topicName, kafkaTopicSet.contains(topicName));
        }
        return result;
    }

    /**
     * check topic 是否存在 默认不检查 kafka内部topic
     */
    public boolean checkExists(String topicName) throws InterruptedException, ExecutionException {
        refreshAdmin();
        Set<String> set = new HashSet<String>(1 << 4);
        set.add(topicName);
        return this.checkExists(set).get(topicName);

    }

    /**
     * 列出某个具体topic 详细配置信息 使用 ConfigResource.Type.TOPIC / ConfigResource.Type.BROKER
     * 来判断资源类型
     */
    public Config descConfigs(ConfigResource.Type type, String resourceName)
            throws InterruptedException, ExecutionException {
        refreshAdmin();
        ConfigResource resource = new ConfigResource(type, resourceName);
        DescribeConfigsResult describeResult = this.adminClient.describeConfigs(Collections.singleton(resource));
        Map<ConfigResource, Config> topicConfig = describeResult.all().get();
        return topicConfig.get(resource);
    }

    /**
     * 批量列出 topic 详细配置信息 使用 ConfigResource.Type.TOPIC / ConfigResource.Type.BROKER
     * 来判断资源类型
     */
    public Map<String, Config> descConfigs(Collection<String> topicNames)
            throws InterruptedException, ExecutionException {
        refreshAdmin();
        Map<String, Config> result = new HashMap<String, Config>(1 << 4);
        List<ConfigResource> list = new ArrayList<ConfigResource>(1 << 4);
        for (String name : topicNames) {
            list.add(new ConfigResource(ConfigResource.Type.TOPIC, name));
        }
        DescribeConfigsResult describeResult = this.adminClient.describeConfigs(list);
        Map<ConfigResource, Config> topicConfig = describeResult.all().get();
        for (ConfigResource res : list) {
            try {
                result.put(res.name(), topicConfig.get(res));
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return result;
    }

    /**
     * 获取某些topics详细信息
     */
    public Map<String, TopicDescription> descTopics(Collection<String> topicNames) {
        refreshAdmin();
        Map<String, TopicDescription> result = new HashMap<String, TopicDescription>(1 << 4);
        Map<String, KafkaFuture<TopicDescription>> describeFutures = this.adminClient.describeTopics(topicNames)
                .values();
        for (String topicName : topicNames) {
            try {
                result.put(topicName, describeFutures.get(topicName).get());
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return result;
    }

    /**
     * 获取topics详细信息
     */
    public TopicDescription descTopic(String topicName) {
        refreshAdmin();
        Collection<String> list = asList(topicName);
        Map<String, KafkaFuture<TopicDescription>> describeFutures = this.adminClient.describeTopics(list)
                .values();
        try {
            return describeFutures.get(topicName).get();
        } catch (InterruptedException e) {
            throw new BusinessException("查看 topic 失败");

        } catch (ExecutionException e) {
            throw new BusinessException("查看 topic 失败");

        }
    }

    /**
     * 列出所有kakfa topic 信息 通过 ListTopicsOptions 可以设置超时间和是否为Kafka内部topic eg: 设置超时时间
     * 设置列出所有topic(包含kafka内部topic ) ListTopicsOptions options = new
     * ListTopicsOptions(); options.listInternal(true); options.timeoutMs(new
     * Integer(3000));
     */
    public Map<String, TopicListing> listTopics(ListTopicsOptions options)
            throws InterruptedException, ExecutionException {
        refreshAdmin();
        ListTopicsResult listTopic = this.adminClient.listTopics(options);
        KafkaFuture<Map<String, TopicListing>> kfuture = listTopic.namesToListings();
        return kfuture.get();
    }

    /**
     * 单独创建topic
     *
     * @throws Exception
     */
    public boolean createTopic(String topicName, int numPartitions, short replicationFactor) throws Exception {
        refreshAdmin();
        return this.createTopic(topicName, numPartitions, replicationFactor, null);
    }

    /**
     * 若 topic不存在则创建 默认只检测非kafka内部的topic
     *
     * @throws Exception
     */
    public boolean createTopicIfNotExists(String topicName, int numPartitions, short replicationFactor, long ttl)
            throws Exception {
        refreshAdmin();
        if (!this.checkExists(topicName)) {
            Map<String, String> configs = new HashMap<>(1 << 3);
            configs.put(TopicConfig.RETENTION_MS_CONFIG, ttl + "");
            configs.put(TopicConfig.DELETE_RETENTION_MS_CONFIG, ttl + "");
            return this.createTopic(topicName, numPartitions, replicationFactor, configs);
        }
        return true;
    }

    /**
     * 若 topic不存在则创建 默认只检测非kafka内部的topic
     *
     * @throws Exception
     */
    public boolean createTopicIfNotExists(String topicName, int numPartitions, short replicationFactor,
                                          Map<String, String> topicConfig) throws Exception {
        refreshAdmin();
        if (this.checkExists(topicName)) {
            return this.createTopic(topicName, numPartitions, replicationFactor, topicConfig);
        }
        return true;
    }

    private boolean createTopic(String topicName, int numPartitions, short replicationFactor,
                                Map<String, String> topicConfig) throws Exception {
        refreshAdmin();
        boolean success = false;
        NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
        newTopic.configs(topicConfig);
        CreateTopicsResult createTopicResult = this.adminClient.createTopics(Collections.singleton(newTopic));

        try {
            createTopicResult.values().get(topicName).get();
            success = true;
        } catch (Exception e) {
            throw e;
        }
        return success;
    }

    /**
     * 指定ttl partitionNumber 与 副本数
     */
    public boolean createTopic(String topicName, int numPartitions, short replicationFactor, long ms) {
        refreshAdmin();
        boolean success = false;
        NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
        Map<String, String> configs = new HashMap<>(1 << 3);
        configs.put(TopicConfig.RETENTION_MS_CONFIG, ms + "");
        configs.put(TopicConfig.DELETE_RETENTION_MS_CONFIG, ms + "");
        newTopic.configs(configs);
        CreateTopicsResult createTopicResult = this.adminClient.createTopics(Collections.singleton(newTopic));
        try {
            createTopicResult.values().get(topicName).isCompletedExceptionally();
            success = true;
        } catch (Exception e) {
            logger.error("", e);
        }
        return success;
    }

    /**
     * 删除topic
     */
    public boolean delete(String topic) {
        refreshAdmin();
        Map<String, Boolean> resultMap = this.delete(Collections.singleton(topic));
        return resultMap.get(topic);
    }

    /**
     * 删除topics
     */
    public Map<String, Boolean> delete(Collection<String> topics) {
        refreshAdmin();
        Map<String, Boolean> result = new HashMap<String, Boolean>(1 << 4);
        DeleteTopicsResult delRes = this.adminClient.deleteTopics(topics);
        Map<String, KafkaFuture<Void>> deleteFutures = delRes.values();
        for (String topicName : topics) {
            boolean flag = false;
            try {
                KafkaFuture<Void> kafkaFutrue = deleteFutures.get(topicName);
                kafkaFutrue.get();
                flag = true;
            } catch (Exception e) {
                logger.error("", e);
            }
            result.put(topicName, flag);
        }
        return result;
    }

}
