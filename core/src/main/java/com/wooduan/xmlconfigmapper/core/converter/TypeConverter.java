package com.wooduan.xmlconfigmapper.core.converter;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/23 21:57
 */
public interface TypeConverter<T>
{
    T read(String value);

    final class NoneTypeConverter implements TypeConverter<Object>
    {
        private NoneTypeConverter()
        {
        }

        @Override
        public Object read(String value)
        {
            return null;
        }
    }
}
