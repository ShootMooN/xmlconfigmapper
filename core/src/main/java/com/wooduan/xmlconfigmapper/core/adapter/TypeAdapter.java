package com.wooduan.xmlconfigmapper.core.adapter;

import com.wooduan.xmlconfigmapper.core.XmlConfigMapperContext;
import org.dom4j.Element;

import java.io.IOException;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/25 14:40
 */
public interface TypeAdapter<T>
{
    String GENERATED_CLASS_SUFFIX = "$$TypeAdapter";

    T fromXml(Element reader, XmlConfigMapperContext context) throws IOException;
}
