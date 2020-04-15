package com.github.shootmoon.xmlconfigmapper.itest;

import com.github.shootmoon.xmlconfigmapper.core.XmlConfigMappers;
import com.github.shootmoon.xmlconfigmapper.core.annotation.XmlConfigMapper;
import com.github.shootmoon.xmlconfigmapper.core.annotation.XmlConfigMapping;

import org.dom4j.Element;
import java.util.List;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/4/14 16:54
 */
@XmlConfigMapper
public interface MyMapper
{
    MyMapper INSTANCE = XmlConfigMappers.getMapper(MyMapper.class);

    @XmlConfigMapping
    List<Catalogue> getCatalogueList(String fileName);

    @XmlConfigMapping
    Catalogue getCatalogue(String fileName);
}
