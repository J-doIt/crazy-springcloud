package com.crazymaker.springcloud.email.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Properties;

@Configuration
@ConditionalOnProperty(prefix = "mail.default", name = "host")
class MailSenderConfiguration
{
    private JavaMailSender mailSender;

    private final MailProperties properties;

    public MailSenderConfiguration(MailProperties properties)
    {
        this.properties = properties;
    }

    @Bean
//    @ConditionalOnMissingBean
    public JavaMailSender javaMailSender()
    {
        if (null != mailSender)
        {
            return mailSender;
        }
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        applyProperties(sender, properties);
        mailSender = sender;
        return mailSender;
    }

    private void applyProperties(JavaMailSenderImpl sender, MailProperties pro)
    {
        sender.setHost(pro.getHost());
        if (pro.getPort() != null)
        {
            sender.setPort(pro.getPort());
        }
        sender.setUsername(pro.getUsername());
        sender.setPassword(pro.getPassword());
        sender.setProtocol(pro.getProtocol());
        if (pro.getDefaultEncoding() != null)
        {
            sender.setDefaultEncoding(pro.getDefaultEncoding().name());
        }
        if (!pro.getProperties().isEmpty())
        {
            sender.setJavaMailProperties(asProperties(pro.getProperties()));
        }
    }

    private Properties asProperties(Map<String, String> source)
    {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }

    /**
     * 修改发送者
     *
     * @param pro smtp属性
     */
    public void changeMailSender(MailProperties pro)
    {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        applyProperties(sender, properties);

        this.mailSender = sender;
    }

    /**
     * 获取发送者
     *
     * @return 发送者
     */
    public JavaMailSender getMailSender()
    {

        return mailSender;
    }
}