package com.wooduan.xmlconfigmapper.processor.field

import com.wooduan.xmlconfigmapper.core.annotation.XmlConfigMapping
import com.wooduan.xmlconfigmapper.processor.util.ProcessingException
import com.wooduan.xmlconfigmapper.processor.xml.XmlChildElement
import com.wooduan.xmlconfigmapper.processor.xml.XmlRootElement
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/25 11:24
 */
class AnnotatedClass @Throws(ProcessingException::class) constructor(e: Element) : XmlRootElement
{
    override val element: TypeElement
    override val nameAsRoot: String
    val simpleClassName: String
    val qualifiedClassName: String
    override val childElements = HashMap<String, XmlChildElement>()

    init
    {
        element = e as TypeElement
        simpleClassName = element.simpleName.toString()
        qualifiedClassName = element.qualifiedName.toString()

        val xmlAnnotation = element.getAnnotation(XmlConfigMapping::class.java)

        nameAsRoot =
                if (xmlAnnotation.name.isEmpty())
                {
                    simpleClassName.decapitalize()
                }
                else
                {
                    xmlAnnotation.name
                }
    }
}