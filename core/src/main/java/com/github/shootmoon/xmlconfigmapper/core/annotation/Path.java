package com.github.shootmoon.xmlconfigmapper.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/4/7 19:44
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Path
{
    String value();
}
