package com.wooduan.xmlconfigmapper.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/17 15:06
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface XmlConfigMapping
{
    String name() default "";
}
