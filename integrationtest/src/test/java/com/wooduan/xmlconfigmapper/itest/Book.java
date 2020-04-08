package com.wooduan.xmlconfigmapper.itest;

import com.wooduan.xmlconfigmapper.core.annotation.Property;
import com.wooduan.xmlconfigmapper.core.annotation.XmlConfigMapping;

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
