package com.crazymaker.springcloud.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WrapperUtils
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WrapperUtils.class);

    /**
     *
     */
    private static final String DEFAULT_REST_CHARSET = "UTF-8";

    /**
     *
     */
    private static final String CODE_NAME = "code";

    /**
     *
     */
    private static final TypeAdapter<String> STRING = new TypeAdapter<String>()
    {
        /**
         *
         */
        @Override
        public String read(JsonReader reader) throws IOException
        {
            JsonToken t = reader.peek();
            String s = reader.nextString();
            if (StringUtils.isEmpty(s))
            {
                return null;
            }
            if (t == JsonToken.NULL)
            {
                return "";
            }
            return s;
        }

        /**
         *
         */
        @Override
        public void write(JsonWriter writer, String value) throws IOException
        {
            if (value == null)
            {
                // 在这里处理null改为空字符串
                writer.value("" );
                return;
            }
            writer.value(value);
        }
    };

    /**
     * 示例创建实现类，用于创建一个pagerequest对象，该对象会在 gson类中用到
     */
    private static final InstanceCreator<Pageable> PAGEABLE_INSTANCE_CREATOR = type -> PageRequest.of(0, 10);

    /**
     *
     */
    private static Gson gson = new GsonBuilder().registerTypeAdapter(String.class, STRING)
            .registerTypeAdapter(Pageable.class, PAGEABLE_INSTANCE_CREATOR).disableHtmlEscaping().serializeNulls()
            .create();

    /**
     * 这个GSON用于拆箱用
     */
    private static Gson gson4DTO = new GsonBuilder().registerTypeAdapter(Pageable.class, PAGEABLE_INSTANCE_CREATOR)
            .registerTypeAdapter(String.class, STRING).disableHtmlEscaping().serializeNulls().create();

    /**
     *
     */
    private WrapperUtils()
    {
    }

    /**
     * @param json
     * @param classOfT
     * @return
     */
    public static <T> T getDto(String json, Class<T> classOfT)
    {
        return gson4DTO.fromJson(json, classOfT);
    }

    /**
     * @param target
     */
    public static String toJson(Object target)
    {
        return gson.toJson(target);
    }

    /**
     * @param request
     * @param classOfT
     * @return
     */
    public static <T> T getDto(HttpServletRequest request, Class<T> classOfT)
    {
        InputStream is = null;
        T target = null;
        try
        {
            is = request.getInputStream();
            String data = IOUtils.toString(is, DEFAULT_REST_CHARSET);

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("WrapperUtils:" + data);
            }

            target = gson4DTO.fromJson(data, classOfT);
        } catch (IOException e)
        {
            LOGGER.error("getDto error [" + request + "][" + classOfT + "]", e);
        } finally
        {
            IOUtils.closeQuietly(is);
        }
        return target;
    }

    /**
     * @param response
     * @param inv
     * @return
     */
    public static String printSuccessJson(Object response)
    {
        Map<String, Object> map = new HashMap<>(16);
        if (null != response)
        {
            map.put("response", response);
        }
        map.put(CODE_NAME, 200);
        return "@" + WrapperUtils.toJson(map);
    }

    /**
     * @param errmsg
     * @param inv
     * @return
     */
    public static String printErrorJson(String errmsg)
    {
        Map<String, Object> map = new HashMap<>(16);
        map.put(CODE_NAME, 500);
        map.put("message", errmsg);
        return "@" + WrapperUtils.toJson(map);
    }

    /**
     * @param inv
     * @return
     */
    public static String printSuccessJson()
    {
        return printSuccessJson(null);
    }

    /**
     * 指定Key 和 value返回
     *
     * @param key
     * @param value
     * @param inv
     * @return
     */
    public static String printSuccessJson(String key, Object value)
    {
        Map<String, Object> map = new HashMap<>(16);
        map.put(key, value);
        return printSuccessJson(map);
    }

    /**
     * @param json
     * @param clazz
     * @return
     */
    public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> clazz)
    {
        Type type = new TypeToken<ArrayList<JsonObject>>()
        {
        }.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);
        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects)
        {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }

    /**
     * @param notes
     * @return
     */
    public static boolean isJson(String notes)
    {
        if (StringUtils.isBlank(notes))
        {
            return false;
        }
        try
        {
            new JsonParser().parse(notes);
            return true;
        } catch (JsonParseException e)
        {
            LOGGER.error("bad json: " + notes);
            return false;
        }
    }
}
