package com.github.shootmoon.xmlconfigmapper.core;

import java.lang.reflect.Constructor;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/18 11:26
 */
public final class XmlConfigMappers
{
    private static final XmlConfigMapperContext context = new XmlConfigMapperContext();

    public static <T> T getMapper(Class<T> clazz)
    {
        try
        {
            StringBuilder qualifiedMapperClassName = new StringBuilder();
            Package packageElement = clazz.getPackage();
            if (packageElement != null)
            {
                String packageName = packageElement.getName();
                if (packageName != null && packageName.length() > 0)
                {
                    qualifiedMapperClassName.append(packageElement.getName());
                    qualifiedMapperClassName.append('.');
                }
            }

            qualifiedMapperClassName.append(clazz.getSimpleName());
            qualifiedMapperClassName.append("$$Mapper");

            Class<T> adapterClass = (Class<T>)Class.forName(qualifiedMapperClassName.toString());
            Constructor<T> constructor = adapterClass.getConstructor(XmlConfigMapperContext.class);
            return constructor.newInstance(context);
        }
        catch (Throwable t)
        {
            throw new RuntimeException(t);
        }
    }
}
