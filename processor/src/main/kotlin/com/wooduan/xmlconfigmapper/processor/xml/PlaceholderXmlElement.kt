package com.wooduan.xmlconfigmapper.processor.xml

import com.squareup.javapoet.TypeSpec
import com.wooduan.xmlconfigmapper.processor.generator.CodeGeneratorHelper
import com.wooduan.xmlconfigmapper.processor.util.getSurroundingClassQualifiedName
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/4/1 20:51
 */
class PlaceholderXmlElement(override val name: String, override val element: Element) : XmlChildElement
{
    override val childElements = LinkedHashMap<String, XmlChildElement>()

    override fun toString(): String =
            when (element)
            {
                is VariableElement -> "field '${element.simpleName}' in class ${element.getSurroundingClassQualifiedName()}"
                is TypeElement     -> element.qualifiedName.toString()
                else               -> throw IllegalArgumentException("Oops, unexpected element type $element. This should never happen.")
            }

    override fun generateReadXmlCode(codeGeneratorHelper: CodeGeneratorHelper): TypeSpec
    {
        return codeGeneratorHelper.generateNestedChildElementBinder(this)
    }
}