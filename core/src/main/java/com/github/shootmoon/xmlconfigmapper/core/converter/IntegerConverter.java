package com.github.shootmoon.xmlconfigmapper.core.converter;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/4/2 16:02
 */
public class IntegerConverter implements TypeConverter<Integer>
{
    @Override
    public Integer read(String value)
    {
        return Integer.valueOf(value);
    }
}
