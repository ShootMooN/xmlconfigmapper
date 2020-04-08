package com.wooduan.xmlconfigmapper.itest;

import com.wooduan.xmlconfigmapper.core.converter.TypeConverter;

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
