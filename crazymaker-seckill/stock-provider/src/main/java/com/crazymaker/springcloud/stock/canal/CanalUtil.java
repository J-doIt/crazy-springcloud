package com.crazymaker.springcloud.stock.canal;

import com.alibaba.otter.canal.protocol.FlatMessage;
import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.util.JsonUtil;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class CanalUtil {


    /**
     * 获取Object标有@Id注解的字段值
     *
     * @param t 对象
     * @return id值
     */
    public static  <T> Object getIdValue(T t,Class<T> clz) {
        Field fieldOfId = getIdField(clz);
        ReflectionUtils.makeAccessible(fieldOfId);
        return ReflectionUtils.getField(fieldOfId, t);
    }

    /**
     * 获取Class标有@Id注解的字段名称
     *
     * @return id字段名称
     */
    public static  <T> Field getIdField(Class<T> clz ) {

        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            Id annotation = field.getAnnotation(Id.class);

            if (annotation != null) {
                return field;
            }
        }

        log.error("PO类未设置@Id注解");
        throw new BusinessException("PO类未设置@Id注解");
    }

    /**
     * 转换Canal的FlatMessage中data成泛型对象
     *
     * @param flatMessage Canal发送MQ信息
     * @return 泛型对象集合
     */
    public static  <T> Set<T> getCanalData(FlatMessage flatMessage, Class<T> type) {
        List<Map<String, String>> sourceData = flatMessage.getData();
        Set<T> targetData = Sets.newHashSetWithExpectedSize(sourceData.size());
        for (Map<String, String> map : sourceData) {
            T t = JsonUtil.mapToPojo(map,type);
            targetData.add(t);
        }
        return targetData;
    }
}
