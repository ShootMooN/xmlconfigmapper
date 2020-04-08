package com.wooduan.xmlconfigmapper.processor.field

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeSpec
import com.wooduan.xmlconfigmapper.processor.generator.CodeGeneratorHelper
import com.wooduan.xmlconfigmapper.processor.xml.XmlChildElement
import java.util.LinkedHashMap
import javax.lang.model.element.VariableElement

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/25 15:30
 */
open class ElementField(element: VariableElement, name: String) : Field(element, name), XmlChildElement
{
    override val childElements = LinkedHashMap<String, XmlChildElement>()

    override fun generateReadXmlCode(codeGeneratorHelper: CodeGeneratorHelper): TypeSpec
    {
        val fromXmlMethod = codeGeneratorHelper.fromXmlMethodBuilder()
                .addCode(accessResolver.resolveSetter("(\$T)${CodeGeneratorHelper.contextParam}.getTypeAdapter(\$T.class).fromXml(${CodeGeneratorHelper.elementParam}, ${CodeGeneratorHelper.contextParam})",
                        ClassName.get(element.asType()), ClassName.get(element.asType())))
                .build()

        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(codeGeneratorHelper.childElementBinderType)
                .addMethod(fromXmlMethod)
                .build()
    }
}