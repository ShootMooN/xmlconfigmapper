package com.github.shootmoon.xmlconfigmapper.core.converter;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/4/2 16:02
 */
public class LongConverter implements TypeConverter<Long>
{
    @Override
    public Long read(String value)
    {
        return Long.valueOf(value);
    }
}
