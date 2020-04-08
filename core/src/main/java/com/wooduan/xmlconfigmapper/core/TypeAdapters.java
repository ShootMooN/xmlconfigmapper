package com.wooduan.xmlconfigmapper.core;

import com.wooduan.xmlconfigmapper.core.adapter.TypeAdapter;
import com.wooduan.xmlconfigmapper.core.adapter.TypeAdapterNotFoundException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/30 20:32
 */
final class TypeAdapters
{
    private final static Map<Type, TypeAdapter<?>> adaptersCache = new HashMap<>();

    public static <T> TypeAdapter<T> get(Class<T> clazz) throws TypeAdapterNotFoundException
    {
        TypeAdapter<T> adapter = (TypeAdapter<T>) adaptersCache.get(clazz);

        if (adapter != null)
        {
            return adapter;
        }
        else
        {
            // try to load TypeAdapter via reflections
            StringBuilder qualifiedTypeAdapterClassName = new StringBuilder();
            try
            {
                Package packageElement = clazz.getPackage();
                if (packageElement != null)
                {
                    String packageName = packageElement.getName();
                    if (packageName != null && packageName.length() > 0)
                    {
                        qualifiedTypeAdapterClassName.append(packageElement.getName());
                        qualifiedTypeAdapterClassName.append('.');
                    }
                }

                qualifiedTypeAdapterClassName.append(clazz.getSimpleName());
                qualifiedTypeAdapterClassName.append(TypeAdapter.GENERATED_CLASS_SUFFIX);

                try
                {
                    Class<TypeAdapter<T>> adapterClass = (Class<TypeAdapter<T>>) Class.forName(qualifiedTypeAdapterClassName.toString());

                    TypeAdapter<T> adapterInstance = adapterClass.newInstance();
                    adaptersCache.put(clazz, adapterInstance);
                    return adapterInstance;
                }
                catch (ClassNotFoundException e)
                {
                    throw new TypeAdapterNotFoundException("No TypeAdapter for class " + clazz.getCanonicalName() + " found. Expected name of the type adapter is " + qualifiedTypeAdapterClassName.toString(), e);
                }
            }
            catch (InstantiationException | IllegalAccessException e)
            {
                throw new TypeAdapterNotFoundException("No TypeAdapter for class " + clazz.getCanonicalName() + " found. Expected name of the type adapter is " + qualifiedTypeAdapterClassName.toString(), e);
            }
        }
    }
}
