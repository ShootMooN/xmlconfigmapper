package com.github.shootmoon.xmlconfigmapper.itest;

import com.github.shootmoon.xmlconfigmapper.core.annotation.Ignore;
import com.github.shootmoon.xmlconfigmapper.core.annotation.Path;
import com.github.shootmoon.xmlconfigmapper.core.annotation.Property;
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

    @Path("type")
    @Property(name = "num")
    Integer typeNum;

    @Ignore
    Book book;

    List<Book> books1;

    @Path("books")
    List<Book> books2;
}
