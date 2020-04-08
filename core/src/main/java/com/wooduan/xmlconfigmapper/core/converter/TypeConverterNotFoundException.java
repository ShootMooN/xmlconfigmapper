package com.wooduan.xmlconfigmapper.core.converter;

import java.io.IOException;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/30 20:30
 */
public class TypeConverterNotFoundException extends IOException
{
    public TypeConverterNotFoundException(String message)
    {
        super(message);
    }
}
