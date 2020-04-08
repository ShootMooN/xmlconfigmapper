package com.wooduan.xmlconfigmapper.core.converter;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/4/2 16:02
 */
public class StringConverter implements TypeConverter<String>
{
    @Override
    public String read(String value)
    {
        return value;
    }
}
