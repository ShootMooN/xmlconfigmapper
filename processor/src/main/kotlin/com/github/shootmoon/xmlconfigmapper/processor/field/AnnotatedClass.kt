package com.github.shootmoon.xmlconfigmapper.processor.field

import com.github.shootmoon.xmlconfigmapper.core.annotation.XmlConfigBean
import com.github.shootmoon.xmlconfigmapper.processor.util.ProcessingException
import com.github.shootmoon.xmlconfigmapper.processor.xml.XmlChildElement
import com.github.shootmoon.xmlconfigmapper.processor.xml.XmlRootElement
import javax.lang.model.element.TypeElement

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/25 11:24
 */
class AnnotatedClass @Throws(ProcessingException::class) constructor(override val element: TypeElement) : XmlRootElement
{
    override val nameAsRoot: String
    val simpleClassName: String
    val qualifiedClassName: String
    override val childElements = HashMap<String, XmlChildElement>()

    init
    {
        simpleClassName = element.simpleName.toString()
        qualifiedClassName = element.qualifiedName.toString()

        val xmlAnnotation = element.getAnnotation(XmlConfigBean::class.java)

        nameAsRoot =
                if (xmlAnnotation == null || xmlAnnotation.name.isEmpty())
                {
                    simpleClassName.decapitalize()
                }
                else
                {
                    xmlAnnotation.name
                }
    }
}