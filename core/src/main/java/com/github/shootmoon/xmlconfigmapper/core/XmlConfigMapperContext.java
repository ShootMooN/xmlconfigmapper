package com.github.shootmoon.xmlconfigmapper.core;

import com.github.shootmoon.xmlconfigmapper.core.adapter.TypeAdapter;
import com.github.shootmoon.xmlconfigmapper.core.adapter.TypeAdapterNotFoundException;
import com.github.shootmoon.xmlconfigmapper.core.converter.TypeConverter;
import com.github.shootmoon.xmlconfigmapper.core.converter.TypeConverterNotFoundException;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/4/7 20:28
 */
public final class XmlConfigMapperContext
{
    private TypeAdapters typeAdapters = new TypeAdapters();

    XmlConfigMapperContext(){}

    public <T> TypeAdapter<T> getTypeAdapter(Class<T> clazz) throws TypeAdapterNotFoundException
    {
        return typeAdapters.get(clazz);
    }
}
