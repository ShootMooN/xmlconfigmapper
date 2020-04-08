package com.wooduan.xmlconfigmapper.core;

import com.wooduan.xmlconfigmapper.core.converter.BooleanConverter;
import com.wooduan.xmlconfigmapper.core.converter.LocalDateTimeConverter;
import com.wooduan.xmlconfigmapper.core.converter.LongConverter;
import com.wooduan.xmlconfigmapper.core.converter.IntegerConverter;
import com.wooduan.xmlconfigmapper.core.converter.TypeConverter;
import com.wooduan.xmlconfigmapper.core.converter.TypeConverterNotFoundException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/30 20:29
 */
final class TypeConverters
{
    private final static Map<Type, TypeConverter<?>> typesMap = new HashMap<>();
    private final static HashSet<String> typeNamesSet = new HashSet<>();

    static
    {
        typesMap.put(Integer.class, new IntegerConverter());
        typesMap.put(Long.class, new LongConverter());
        typesMap.put(Boolean.class, new BooleanConverter());
        typesMap.put(LocalDateTime.class, new LocalDateTimeConverter());

        typeNamesSet.add(Integer.class.getCanonicalName());
        typeNamesSet.add(Long.class.getCanonicalName());
        typeNamesSet.add(Boolean.class.getCanonicalName());
        typeNamesSet.add(LocalDateTime.class.getCanonicalName());
    }

    public static boolean contains(String typeString)
    {
        return typeNamesSet.contains(typeString);
    }

    public static <T> TypeConverter<T> get(Class<T> clazz) throws TypeConverterNotFoundException
    {
        TypeConverter<T> converter = (TypeConverter<T>) typesMap.get(clazz);
        if (converter != null)
        {
            return converter;
        }
        else
        {
            throw new TypeConverterNotFoundException("No TypeConverter has been found for " + clazz.toString());
        }
    }
}
