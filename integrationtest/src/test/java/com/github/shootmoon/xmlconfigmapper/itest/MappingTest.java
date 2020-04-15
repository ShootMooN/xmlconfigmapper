package com.github.shootmoon.xmlconfigmapper.itest;

import org.junit.Test;

import java.net.URL;
import java.util.List;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/17 16:01
 */
public class MappingTest
{
    @Test
    public void test1()
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource("books.xml");

        List<Catalogue> catalogues = MyMapper.INSTANCE.getCatalogueList(url.getFile());

        Catalogue catalogue = MyMapper.INSTANCE.getCatalogue(url.getFile());

        System.out.println();
    }
}
