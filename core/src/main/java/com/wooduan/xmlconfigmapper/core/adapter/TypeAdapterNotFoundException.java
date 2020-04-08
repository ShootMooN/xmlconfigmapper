package com.wooduan.xmlconfigmapper.core.adapter;

import java.io.IOException;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/30 20:32
 */
public class TypeAdapterNotFoundException extends IOException
{
    public TypeAdapterNotFoundException(String message) {
        super(message);
    }

    public TypeAdapterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
