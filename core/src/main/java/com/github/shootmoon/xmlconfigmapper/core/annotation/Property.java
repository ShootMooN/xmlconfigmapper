package com.github.shootmoon.xmlconfigmapper.core.annotation;

import com.github.shootmoon.xmlconfigmapper.core.converter.TypeConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/25 16:33
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Property
{
    String name() default "";

    Class<? extends TypeConverter> converter() default TypeConverter.NoneTypeConverter.class;
}
