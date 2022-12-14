package com.crazymaker.springcloud.base.config;

import com.crazymaker.springcloud.base.core.CustomedSessionIdResolver;
import com.crazymaker.springcloud.common.constants.SessionConstants;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.ConfigureNotifyKeyspaceEventsAction;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executor;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Exposes the {@link SessionRepositoryFilter} as a bean named
 * {@code springSessionRepositoryFilter}. In order to use this a single
 * {@link RedisConnectionFactory} must be exposed as a Bean.
 *
 * @author Rob Winch
 * @author Eddú Meléndez
 * @author Vedran Pavic
 * @see EnableRedisHttpSession
 * @since 1.0
 */
@Configuration
@EnableScheduling
public class CustomedRedisHttpSessionConfiguration
//        extends SpringHttpSessionConfiguration
        implements BeanClassLoaderAware, EmbeddedValueResolverAware, ImportAware,
        SchedulingConfigurer
{


    static final String DEFAULT_CLEANUP_CRON = "0 * * * * *";

    //    private Integer maxInactiveIntervalInSeconds = MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS;
    private Integer maxInactiveIntervalInSeconds = SessionConstants.SESSION_TIME_OUT;

    private String redisNamespace = RedisOperationsSessionRepository.DEFAULT_NAMESPACE;

    private RedisFlushMode redisFlushMode = RedisFlushMode.ON_SAVE;

    private String cleanupCron = DEFAULT_CLEANUP_CRON;

    private ConfigureRedisAction configureRedisAction = new ConfigureNotifyKeyspaceEventsAction();

    private RedisConnectionFactory redisConnectionFactory;

    private RedisSerializer<Object> defaultRedisSerializer;

    private ApplicationEventPublisher applicationEventPublisher;

    private Executor redisTaskExecutor;

    private Executor redisSubscriptionExecutor;

    private ClassLoader classLoader;

    private StringValueResolver embeddedValueResolver;
    private RedisOperationsSessionRepository sessionRepository;

    @DependsOn("httpSessionIdResolver" )
    @Bean
    public RedisOperationsSessionRepository sessionRepository(CustomedSessionIdResolver httpSessionIdResolver)
    {
        RedisTemplate<Object, Object> redisTemplate = createRedisTemplate();
        RedisOperationsSessionRepository sessionRepository =
                new RedisOperationsSessionRepository(redisTemplate);

        sessionRepository.setApplicationEventPublisher(this.applicationEventPublisher);
        if (this.defaultRedisSerializer != null)
        {
            sessionRepository.setDefaultSerializer(this.defaultRedisSerializer);
        }
        sessionRepository
                .setDefaultMaxInactiveInterval(this.maxInactiveIntervalInSeconds);
        if (StringUtils.hasText(this.redisNamespace))
        {
            sessionRepository.setRedisKeyNamespace(this.redisNamespace + ":" + SessionConstants.REDIS_SESSION_KEY_PREFIX);
        }
        sessionRepository.setRedisFlushMode(RedisFlushMode.IMMEDIATE);
//        sessionRepository.setRedisFlushMode(this.redisFlushMode);
        int database = resolveDatabase();
        sessionRepository.setDatabase(database);

        this.sessionRepository = sessionRepository;
        return sessionRepository;
    }

    @DependsOn("sessionRepository" )
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisOperationsSessionRepository sessionRepository)
    {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(this.redisConnectionFactory);
        if (this.redisTaskExecutor != null)
        {
            container.setTaskExecutor(this.redisTaskExecutor);
        }
        if (this.redisSubscriptionExecutor != null)
        {
            container.setSubscriptionExecutor(this.redisSubscriptionExecutor);
        }
        container.addMessageListener(sessionRepository, Arrays.asList(
                new ChannelTopic(sessionRepository.getSessionDeletedChannel()),
                new ChannelTopic(sessionRepository.getSessionExpiredChannel())));
        container.addMessageListener(sessionRepository,
                Collections.singletonList(new PatternTopic(
                        sessionRepository.getSessionCreatedChannelPrefix() + "*" )));
        return container;
    }

    @Bean
    public InitializingBean enableRedisKeyspaceNotificationsInitializer()
    {
        return new EnableRedisKeyspaceNotificationsInitializer(
                this.redisConnectionFactory, this.configureRedisAction);
    }

    public void setMaxInactiveIntervalInSeconds(int maxInactiveIntervalInSeconds)
    {
        this.maxInactiveIntervalInSeconds = maxInactiveIntervalInSeconds;
    }

    public void setRedisNamespace(String namespace)
    {
        this.redisNamespace = namespace;
    }

    public void setRedisFlushMode(RedisFlushMode redisFlushMode)
    {
        Assert.notNull(redisFlushMode, "redisFlushMode cannot be null" );
        this.redisFlushMode = redisFlushMode;
    }

    public void setCleanupCron(String cleanupCron)
    {
        this.cleanupCron = cleanupCron;
    }

    /**
     * Sets the action to perform for configuring Redis.
     *
     * @param configureRedisAction the configureRedis to set. The default is
     *                             {@link ConfigureNotifyKeyspaceEventsAction}.
     */
    @Autowired(required = false)
    public void setConfigureRedisAction(ConfigureRedisAction configureRedisAction)
    {
        this.configureRedisAction = configureRedisAction;
    }

    @Autowired
    public void setRedisConnectionFactory(
            @SpringSessionRedisConnectionFactory ObjectProvider<RedisConnectionFactory> springSessionRedisConnectionFactory,
            ObjectProvider<RedisConnectionFactory> redisConnectionFactory)
    {
        RedisConnectionFactory redisConnectionFactoryToUse = springSessionRedisConnectionFactory
                .getIfAvailable();
        if (redisConnectionFactoryToUse == null)
        {
            redisConnectionFactoryToUse = redisConnectionFactory.getObject();
        }
        this.redisConnectionFactory = redisConnectionFactoryToUse;
    }

    @Autowired(required = false)
    @Qualifier("springSessionDefaultRedisSerializer" )
    public void setDefaultRedisSerializer(
            RedisSerializer<Object> defaultRedisSerializer)
    {
        this.defaultRedisSerializer = defaultRedisSerializer;
    }

    @Autowired
    public void setApplicationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Autowired(required = false)
    @Qualifier("springSessionRedisTaskExecutor" )
    public void setRedisTaskExecutor(Executor redisTaskExecutor)
    {
        this.redisTaskExecutor = redisTaskExecutor;
    }

    @Autowired(required = false)
    @Qualifier("springSessionRedisSubscriptionExecutor" )
    public void setRedisSubscriptionExecutor(Executor redisSubscriptionExecutor)
    {
        this.redisSubscriptionExecutor = redisSubscriptionExecutor;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver)
    {
        this.embeddedValueResolver = resolver;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata)
    {
        Map<String, Object> attributeMap =
                importMetadata.getAnnotationAttributes(EnableRedisHttpSession.class.getName());
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(attributeMap);

        if (null != attributes)
        {
            this.maxInactiveIntervalInSeconds =
                    attributes.getNumber("maxInactiveIntervalInSeconds" );
            String redisNamespaceValue =
                    attributes.getString("redisNamespace" );
            if (StringUtils.hasText(redisNamespaceValue))
            {
                this.redisNamespace =
                        this.embeddedValueResolver.resolveStringValue(redisNamespaceValue);
            }
            this.redisFlushMode = attributes.getEnum("redisFlushMode" );
            String cleanupCron = attributes.getString("cleanupCron" );
            if (StringUtils.hasText(cleanupCron))
            {
                this.cleanupCron = cleanupCron;
            }
        }
    }

    @Order(HIGHEST_PRECEDENCE)
    @DependsOn("sessionRepository" )
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar)
    {
        taskRegistrar.addCronTask(() -> sessionRepository.cleanupExpiredSessions(),
                this.cleanupCron);
    }

    private RedisTemplate<Object, Object> createRedisTemplate()
    {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        if (this.defaultRedisSerializer != null)
        {
            redisTemplate.setDefaultSerializer(this.defaultRedisSerializer);
        }
        // key 的序列化采用 StringRedisSerializer
        // value 值的序列化采用 GenericJackson2JsonRedisSerializer
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        redisTemplate.setConnectionFactory(this.redisConnectionFactory);
        redisTemplate.setBeanClassLoader(this.classLoader);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    private int resolveDatabase()
    {
        if (ClassUtils.isPresent("io.lettuce.core.RedisClient", null)
                && this.redisConnectionFactory instanceof LettuceConnectionFactory)
        {
            return ((LettuceConnectionFactory) this.redisConnectionFactory).getDatabase();
        }
        if (ClassUtils.isPresent("redis.clients.jedis.Jedis", null)
                && this.redisConnectionFactory instanceof JedisConnectionFactory)
        {
            return ((JedisConnectionFactory) this.redisConnectionFactory).getDatabase();
        }
        return RedisOperationsSessionRepository.DEFAULT_DATABASE;
    }

    /**
     * Ensures that Redis is configured to send keyspace notifications. This is important
     * to ensure that expiration and deletion of sessions trigger SessionDestroyedEvents.
     * Without the SessionDestroyedEvent resources may not getStr cleaned up properly. For
     * example, the mapping of the Session to WebSocket connections may not getStr cleaned
     * up.
     */
    static class EnableRedisKeyspaceNotificationsInitializer implements InitializingBean
    {

        private final RedisConnectionFactory connectionFactory;

        private ConfigureRedisAction configure;

        EnableRedisKeyspaceNotificationsInitializer(
                RedisConnectionFactory connectionFactory,
                ConfigureRedisAction configure)
        {
            this.connectionFactory = connectionFactory;
            this.configure = configure;
        }

        @Override
        public void afterPropertiesSet() throws Exception
        {
            if (this.configure == ConfigureRedisAction.NO_OP)
            {
                return;
            }
            RedisConnection connection = this.connectionFactory.getConnection();
            try
            {
                this.configure.configure(connection);
            } finally
            {
                try
                {
                    connection.close();
                } catch (Exception ex)
                {
                    LogFactory.getLog(getClass()).error("Error closing RedisConnection",
                            ex);
                }
            }
        }

    }


    /**
     * 配置 ID 解析器，从 header  解析id
     *
     * @return
     */
    @Bean("httpSessionIdResolver" )
    public CustomedSessionIdResolver httpSessionIdResolver()
    {
        return new CustomedSessionIdResolver();
    }

}
