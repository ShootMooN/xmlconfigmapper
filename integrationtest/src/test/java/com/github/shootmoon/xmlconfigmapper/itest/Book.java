package com.github.shootmoon.xmlconfigmapper.itest;

import com.github.shootmoon.xmlconfigmapper.core.annotation.Property;
import com.github.shootmoon.xmlconfigmapper.core.annotation.XmlConfigMapping;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/31 10:31
 */
@XmlConfigMapping
public class Book
{
    @Property(converter = MyIntArrayConverter.class)
    Integer[] pages;
}
