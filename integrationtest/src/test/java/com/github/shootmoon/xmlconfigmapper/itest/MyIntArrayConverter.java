package com.github.shootmoon.xmlconfigmapper.itest;

import com.github.shootmoon.xmlconfigmapper.core.converter.TypeConverter;

import java.util.Arrays;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/4/2 13:42
 */
public class MyIntArrayConverter implements TypeConverter<Integer[]>
{
    @Override
    public Integer[] read(String value)
    {
        return Arrays.stream(value.split("\\|")).map(p -> Integer.valueOf(p)).toArray(Integer[]::new);
    }
}
