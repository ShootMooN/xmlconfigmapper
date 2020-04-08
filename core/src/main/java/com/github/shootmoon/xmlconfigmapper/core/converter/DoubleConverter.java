package com.github.shootmoon.xmlconfigmapper.core.converter;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/4/2 16:02
 */
public class DoubleConverter implements TypeConverter<Double>
{
    @Override
    public Double read(String value)
    {
        return Double.valueOf(value);
    }
}
