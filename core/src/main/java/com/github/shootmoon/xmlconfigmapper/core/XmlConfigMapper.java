package com.github.shootmoon.xmlconfigmapper.core;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/18 11:26
 */
public final class XmlConfigMapper
{
    public static final class Builder
    {
        private XmlConfigMapperContext config = new XmlConfigMapperContext();

        public XmlConfigMapper build()
        {
            return new XmlConfigMapper(config);
        }
    }

    private final XmlConfigMapperContext context;

    private XmlConfigMapper(XmlConfigMapperContext context)
    {
        this.context = context;
    }

    public <T> T read(String fileName, Class<T> clazz) throws IOException, DocumentException
    {
        File inputXml = new File(fileName);
        SAXReader saxReader = new SAXReader();
        Element rootElement = saxReader.read(inputXml).getRootElement();

        return read(rootElement, clazz);
    }

    public <T> T read(Element rootElement, Class<T> clazz) throws IOException
    {
        T value = (T) TypeAdapters.get(clazz).fromXml(rootElement, context);

        return value;
    }
}
