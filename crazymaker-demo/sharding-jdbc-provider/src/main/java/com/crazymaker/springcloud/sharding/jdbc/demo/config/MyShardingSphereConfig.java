package com.crazymaker.springcloud.sharding.jdbc.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.google.common.base.Preconditions;
import org.apache.shardingsphere.core.yaml.swapper.MasterSlaveRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.core.yaml.swapper.ShardingRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.core.yaml.swapper.impl.ShadowRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.encrypt.yaml.swapper.EncryptRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.shardingjdbc.api.EncryptDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.api.ShadowDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.spring.boot.common.SpringBootPropertiesConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.encrypt.EncryptRuleCondition;
import org.apache.shardingsphere.shardingjdbc.spring.boot.encrypt.SpringBootEncryptRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.masterslave.MasterSlaveRuleCondition;
import org.apache.shardingsphere.shardingjdbc.spring.boot.masterslave.SpringBootMasterSlaveRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.shadow.ShadowRuleCondition;
import org.apache.shardingsphere.shardingjdbc.spring.boot.shadow.SpringBootShadowRuleConfigurationProperties;
import org.apache.shardingsphere.shardingjdbc.spring.boot.sharding.ShardingRuleCondition;
import org.apache.shardingsphere.shardingjdbc.spring.boot.sharding.SpringBootShardingRuleConfigurationProperties;
import org.apache.shardingsphere.spring.boot.datasource.DataSourcePropertiesSetterHolder;
import org.apache.shardingsphere.spring.boot.util.DataSourceUtil;
import org.apache.shardingsphere.spring.boot.util.PropertyUtil;
import org.apache.shardingsphere.transaction.spring.ShardingTransactionTypeScanner;
import org.apache.shardingsphere.underlying.common.config.inline.InlineExpressionParser;
import org.apache.shardingsphere.underlying.common.exception.ShardingSphereException;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义shardingsphere配置，
 * 实际拷贝自原配置文件，
 * 只做少量改动
 **/
@Configuration
@ComponentScan({"org.apache.shardingsphere.spring.boot.converter"})
@EnableConfigurationProperties({SpringBootShardingRuleConfigurationProperties.class, SpringBootMasterSlaveRuleConfigurationProperties.class, SpringBootEncryptRuleConfigurationProperties.class, SpringBootPropertiesConfigurationProperties.class, SpringBootShadowRuleConfigurationProperties.class})
@AutoConfigureBefore({DruidDataSourceAutoConfigure.class, DataSourceAutoConfiguration.class})
public class MyShardingSphereConfig implements EnvironmentAware {
    private final SpringBootShardingRuleConfigurationProperties shardingRule;
    private final SpringBootMasterSlaveRuleConfigurationProperties masterSlaveRule;
    private final SpringBootEncryptRuleConfigurationProperties encryptRule;
    private final SpringBootShadowRuleConfigurationProperties shadowRule;
    private final SpringBootPropertiesConfigurationProperties props;
    private final Map<String, DataSource> dataSourceMap = new LinkedHashMap();
    private static final String JNDI_NAME = "jndi-name";
    private static final String DRUID_FILTER_PREFIX = "filters";


    @Bean
    @Conditional({ShardingRuleCondition.class})
    public DataSource shardingDataSource() throws SQLException {
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, (new ShardingRuleConfigurationYamlSwapper()).swap(shardingRule), props.getProps());
    }

    @Bean
    @Conditional({MasterSlaveRuleCondition.class})
    public DataSource masterSlaveDataSource() throws SQLException {
        return MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, (new MasterSlaveRuleConfigurationYamlSwapper()).swap(masterSlaveRule), props.getProps());
    }

    @Bean
    @Conditional({EncryptRuleCondition.class})
    public DataSource encryptDataSource() throws SQLException {
        return EncryptDataSourceFactory.createDataSource((DataSource) dataSourceMap.values().iterator().next(), (new EncryptRuleConfigurationYamlSwapper()).swap(encryptRule), props.getProps());
    }

    @Bean
    @Conditional({ShadowRuleCondition.class})
    public DataSource shadowDataSource() throws SQLException {
        return ShadowDataSourceFactory.createDataSource(dataSourceMap, (new ShadowRuleConfigurationYamlSwapper()).swap(shadowRule), props.getProps());
    }

    @Bean
    public ShardingTransactionTypeScanner shardingTransactionTypeScanner() {
        return new ShardingTransactionTypeScanner();
    }

    @Override
    public final void setEnvironment(Environment environment) {
        String prefix = "spring.shardingsphere.datasource.";

        for (String dsName : getDataSourceNames(environment, prefix)) {
            try {
                dataSourceMap.put(dsName, getDataSource(environment, prefix, dsName));
            } catch (ReflectiveOperationException reflectiveOperationException) {
                throw new ShardingSphereException("Can't find datasource type!", reflectiveOperationException);
            } catch (NamingException namingException) {
                throw new ShardingSphereException("Can't find JNDI datasource!", namingException);
            } catch (SQLException sqlException) {
                throw new ShardingSphereException("set druidDatasource filters failed!", sqlException);
            }
        }

    }

    private List<String> getDataSourceNames(Environment environment, String prefix) {
        StandardEnvironment standardEnv = (StandardEnvironment) environment;
        standardEnv.setIgnoreUnresolvableNestedPlaceholders(true);
        List<String> result;
        if (null == standardEnv.getProperty(prefix + "name")) {
            result = (new InlineExpressionParser(standardEnv.getProperty(prefix + "names"))).splitAndEvaluate();
        } else {
            result = Collections.singletonList(standardEnv.getProperty(prefix + "name"));
        }
        return result;
    }

    private DataSource getDataSource(Environment environment, String prefix, String dataSourceName) throws ReflectiveOperationException, NamingException, SQLException {
        Map<String, Object> dataSourceProps = (Map) PropertyUtil.handle(environment, prefix + dataSourceName.trim(), Map.class);
        Preconditions.checkState(!dataSourceProps.isEmpty(), "Wrong datasource properties!");
        if (dataSourceProps.containsKey(JNDI_NAME)) {
            return getJndiDataSource(dataSourceProps.get(JNDI_NAME).toString());
        } else {
            DataSource result = DataSourceUtil.getDataSource(dataSourceProps.get("type").toString(), dataSourceProps);

            //*******适配druidDataSource，添加filter,否则不能实现sql,防火墙等监控功能！！！**************
            if (result instanceof DruidDataSource) {
                if (dataSourceProps.get(DRUID_FILTER_PREFIX) != null) {
                    ((DruidDataSource) result).setFilters(dataSourceProps.get(DRUID_FILTER_PREFIX).toString());
                }
            }
            DataSourcePropertiesSetterHolder.getDataSourcePropertiesSetterByType(dataSourceProps.get("type").toString()).ifPresent((dataSourcePropertiesSetter) ->
            {
                dataSourcePropertiesSetter.propertiesSet(environment, prefix, dataSourceName, result);
            });
            return result;
        }
    }

    private DataSource getJndiDataSource(String jndiName) throws NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setResourceRef(true);
        bean.setJndiName(jndiName);
        bean.setProxyInterface(DataSource.class);
        bean.afterPropertiesSet();
        return (DataSource) bean.getObject();
    }

    public MyShardingSphereConfig(SpringBootShardingRuleConfigurationProperties shardingRule, SpringBootMasterSlaveRuleConfigurationProperties masterSlaveRule, SpringBootEncryptRuleConfigurationProperties encryptRule, SpringBootShadowRuleConfigurationProperties shadowRule, SpringBootPropertiesConfigurationProperties props) {
        this.shardingRule = shardingRule;
        this.masterSlaveRule = masterSlaveRule;
        this.encryptRule = encryptRule;
        this.shadowRule = shadowRule;
        this.props = props;
    }
} 


