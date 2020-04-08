package com.github.shootmoon.xmlconfigmapper.core.adapter;

import com.github.shootmoon.xmlconfigmapper.core.XmlConfigMapperContext;
import org.dom4j.Element;

import java.io.IOException;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/30 16:48
 */
public interface ChildElementBinder<T>
{
    void fromXml(Element element, T value, XmlConfigMapperContext context) throws IOException;
}
