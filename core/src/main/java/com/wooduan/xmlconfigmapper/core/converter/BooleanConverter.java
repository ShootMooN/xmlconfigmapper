package com.wooduan.xmlconfigmapper.core.converter;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/4/2 16:02
 */
public class BooleanConverter implements TypeConverter<Boolean>
{
    @Override
    public Boolean read(String value)
    {
        return Boolean.valueOf(value);
    }
}
