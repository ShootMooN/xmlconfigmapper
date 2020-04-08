package com.github.shootmoon.xmlconfigmapper.itest;

import com.github.shootmoon.xmlconfigmapper.core.annotation.Ignore;
import com.github.shootmoon.xmlconfigmapper.core.annotation.XmlConfigMapping;

import java.util.List;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/31 10:31
 */
@XmlConfigMapping
public class Catalogue
{
    Integer id;

    @Ignore
    Book book;

    List<Book> books;
}
