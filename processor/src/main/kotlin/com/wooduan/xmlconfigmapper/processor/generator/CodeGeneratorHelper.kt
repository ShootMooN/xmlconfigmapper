package com.wooduan.xmlconfigmapper.processor.generator

import com.squareup.javapoet.*
import com.wooduan.xmlconfigmapper.core.XmlConfigMapperContext
import com.wooduan.xmlconfigmapper.core.adapter.ChildElementBinder
import com.wooduan.xmlconfigmapper.core.adapter.NestedChildElementBinder
import com.wooduan.xmlconfigmapper.processor.field.FieldAccessResolver
import com.wooduan.xmlconfigmapper.processor.xml.XmlElement
import java.io.IOException
import java.util.HashMap
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/23 21:29
 */
class CodeGeneratorHelper(val valueType: ClassName)
{
    companion object PARAMS
    {
        const val valueParam = "value"
        const val elementParam = "element"
        const val contextParam = "context"
        const val childElementBindersParam = "childElementBinders"
    }

    val childElementBinderType = ParameterizedTypeName.get(ClassName.get(ChildElementBinder::class.java), valueType)
    val nestedChildElementBinderType = ParameterizedTypeName.get(ClassName.get(NestedChildElementBinder::class.java), valueType)

    fun fromXmlMethodBuilder() =
            MethodSpec.methodBuilder("fromXml")
                    .addAnnotation(Override::class.java)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(org.dom4j.Element::class.java, elementParam)
                    .addParameter(valueType, valueParam)
                    .addParameter(XmlConfigMapperContext::class.java, contextParam)
                    .addException(IOException::class.java)

    fun assignViaTypeConverter(element: Element, accessResolver: FieldAccessResolver, customTypeConverterQualifiedClassName: String?) =
        when
        {
            customTypeConverterQualifiedClassName != null ->
            {
                val fieldName = CustomTypeConverterFieldNameManager.getFieldNameForConverter(customTypeConverterQualifiedClassName)
                accessResolver.resolveSetter("$fieldName.read($elementParam.getText())")
            }
            else ->
            {
                accessResolver.resolveSetter("$contextParam.getTypeConverter(%s.class).read($elementParam.getText())".format(element.asType()))
            }
        }

    fun generateNestedChildElementBinder(element: XmlElement): TypeSpec
    {
        val initializerBuilder = CodeBlock.builder()

        if (element.hasChildElements())
        {
            val childBinderTypeMap = ParameterizedTypeName.get(ClassName.get(HashMap::class.java), ClassName.get(String::class.java), childElementBinderType)
            initializerBuilder.addStatement("$childElementBindersParam = new \$T()", childBinderTypeMap)

            for ((xmlName, xmlElement) in element.childElements)
            {
                initializerBuilder.addStatement("$childElementBindersParam.put(\$S, \$L)", xmlName,
                        xmlElement.generateReadXmlCode(this))
            }
        }

        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(nestedChildElementBinderType)
                .addInitializerBlock(initializerBuilder.build())
                .build()
    }
}