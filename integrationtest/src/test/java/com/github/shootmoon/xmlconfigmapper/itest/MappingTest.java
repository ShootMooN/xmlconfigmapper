package com.github.shootmoon.xmlconfigmapper.itest;

import com.github.shootmoon.xmlconfigmapper.core.XmlConfigMapper;
import org.dom4j.DocumentException;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/17 16:01
 */
public class MappingTest
{
    @Test
    public void test1() throws IOException, DocumentException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource("books.xml");

        Catalogue catalogue = new XmlConfigMapper.Builder().build().read(url.getFile(), Catalogue.class);
        System.out.println();
    }
}
