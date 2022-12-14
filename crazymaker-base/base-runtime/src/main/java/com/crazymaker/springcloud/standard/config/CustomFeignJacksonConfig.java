package com.crazymaker.springcloud.standard.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;


//@Configuration
public class CustomFeignJacksonConfig
{

    /**
     * 反序列化
     *
     * @return 解码器
     */
    @Bean
    public Decoder feignDecoder()
    {
        ObjectMapper objectMapper = customObjectMapper();
        HttpMessageConverter jacksonConverter = new Jackson2HttpMessageConverter(objectMapper);
        ObjectFactory<HttpMessageConverters> objectFactory =
                () -> new HttpMessageConverters(jacksonConverter);
        return new ResponseEntityDecoder(new SpringDecoder(objectFactory));
    }

    /**
     * 序列化
     *
     * @return 编码器
     */
    @Bean
    public Encoder feignEncoder()
    {
        HttpMessageConverter jacksonConverter =
                new MappingJackson2HttpMessageConverter(customObjectMapper());
        ObjectFactory<HttpMessageConverters> objectFactory =
                () -> new HttpMessageConverters(jacksonConverter);
        return new SpringEncoder(objectFactory);
    }

    public ObjectMapper customObjectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();
           /* //定制ObjectMapper

            // 如果为空则不输出
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            // 对于空的对象转json的时候不抛出错误
            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            // 禁用序列化日期为timestamps
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            // 禁用遇到未知属性抛出异常
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            // 视空字符转为null
            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

            // 低层级配置
            // 取消对非ASCII字符的转码
            objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
*/
        //允许解析注释,该属性默认是false，因此必须显式允许
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        // 允许属性名称没有引号
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许单引号
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);


        return objectMapper;
    }

    class Jackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter
    {


        Jackson2HttpMessageConverter(ObjectMapper objectMapper)
        {
            super(objectMapper);

            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.valueOf(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8" ));
            mediaTypes.add(MediaType.APPLICATION_JSON);
            mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
            setSupportedMediaTypes(mediaTypes);
        }
    }

}
