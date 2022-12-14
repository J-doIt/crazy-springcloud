package com.crazymaker.springcloud.standard.hibernate;

import com.crazymaker.springcloud.common.distribute.idGenerator.IdGenerator;
import com.crazymaker.springcloud.distribute.idGenerator.impl.SnowflakeIdGenerator;

import java.util.LinkedHashMap;
import java.util.Map;

public class SnowflakeIdGeneratorFactory
{

    private Map<String, SnowflakeIdGenerator> generatorMap;

    public SnowflakeIdGeneratorFactory()
    {
        generatorMap = new LinkedHashMap<>();
    }

    public synchronized IdGenerator getIdGenerator(String type)
    {
        if (generatorMap.containsKey(type))
        {
            return generatorMap.get(type);
        }

        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(type);
        generatorMap.put(type, idGenerator);
        return idGenerator;
    }

}
