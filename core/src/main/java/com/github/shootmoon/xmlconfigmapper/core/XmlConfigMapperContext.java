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
    private TypeConverters typeConverters = new TypeConverters();
    private TypeAdapters typeAdapters = new TypeAdapters();

    XmlConfigMapperContext(){}

    public <T> TypeAdapter<T> getTypeAdapter(Class<T> clazz) throws TypeAdapterNotFoundException
    {
        return typeAdapters.get(clazz);
    }

    public <T> TypeConverter<T> getTypeConverter(Class<T> clazz) throws TypeConverterNotFoundException
    {
        return typeConverters.get(clazz);
    }
}
