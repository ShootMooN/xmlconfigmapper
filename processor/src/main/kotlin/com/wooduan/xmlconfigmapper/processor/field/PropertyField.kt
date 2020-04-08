package com.wooduan.xmlconfigmapper.processor.field

import com.squareup.javapoet.TypeSpec
import com.wooduan.xmlconfigmapper.processor.generator.CodeGeneratorHelper
import com.wooduan.xmlconfigmapper.processor.xml.XmlChildElement
import java.util.LinkedHashMap
import javax.lang.model.element.VariableElement

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/23 20:47
 */
class PropertyField(element: VariableElement, name: String, val converterQualifiedName: String? = null): Field(element, name), XmlChildElement
{
    override val childElements = LinkedHashMap<String, XmlChildElement>()

    override fun generateReadXmlCode(codeGeneratorHelper: CodeGeneratorHelper): TypeSpec
    {
        val fromXmlMethod = codeGeneratorHelper.fromXmlMethodBuilder()
                .addCode(codeGeneratorHelper.assignViaTypeConverter(element, accessResolver, converterQualifiedName))
                .build()

        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(codeGeneratorHelper.childElementBinderType)
                .addMethod(fromXmlMethod)
                .build()
    }
}