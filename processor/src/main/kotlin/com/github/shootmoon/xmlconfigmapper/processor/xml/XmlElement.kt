package com.github.shootmoon.xmlconfigmapper.processor.xml

import com.github.shootmoon.xmlconfigmapper.core.annotation.Path
import com.github.shootmoon.xmlconfigmapper.processor.util.ProcessingException
import com.github.shootmoon.xmlconfigmapper.processor.util.isList

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/24 15:37
 */
interface XmlElement
{
    val element: javax.lang.model.element.Element

    val childElements: Map<String, XmlChildElement>

    fun hasChildElements() = childElements.isNotEmpty()

    fun addChildElement(toInsert: XmlChildElement)
    {
        var currentElement = this

        if (toInsert.element.isList())
        {
            val path =  toInsert.element.getAnnotation(Path::class.java)?.let { it.value } ?: toInsert.element.simpleName.toString()
            val childElement = currentElement.childElements[path]
            if(childElement != null)
            {
                throw ProcessingException(toInsert.element, "Oops, duplicate $path. This should never happen.")
            }

            val placeholderElement = PlaceholderXmlElement(path, currentElement.element)
            (currentElement.childElements as MutableMap)[path] = placeholderElement
            currentElement = placeholderElement
        }

        val existingElement = currentElement.childElements[toInsert.name]
        if (existingElement != null)
        {
            throw ProcessingException(toInsert.element, "Conflict: $toInsert is in conflict with $existingElement. Maybe both have the same xml name '${toInsert.name}' (you can change that via annotations).")
        }

        (currentElement.childElements as MutableMap)[toInsert.name] = toInsert
    }
}