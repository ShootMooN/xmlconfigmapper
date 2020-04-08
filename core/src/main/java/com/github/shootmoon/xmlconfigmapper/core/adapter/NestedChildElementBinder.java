package com.github.shootmoon.xmlconfigmapper.core.adapter;


import com.github.shootmoon.xmlconfigmapper.core.XmlConfigMapperContext;
import org.dom4j.Element;

import java.io.IOException;
import java.util.Map;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/31 16:34
 */
public abstract class NestedChildElementBinder<T> implements ChildElementBinder<T>
{
    public Map<String, ChildElementBinder<T>> childElementBinders = null;

    @Override
    public void fromXml(Element element, T value, XmlConfigMapperContext context) throws IOException
    {
        if(childElementBinders == null)
        {
            return;
        }

        for(Object childElementObj : element.elements())
        {
            Element childElement = (Element)childElementObj;
            ChildElementBinder<T> childElementBinder = childElementBinders.get(childElement.getName());
            if (childElementBinder != null)
            {
                childElementBinder.fromXml(childElement, value, context);
            }
        }
    }
}
