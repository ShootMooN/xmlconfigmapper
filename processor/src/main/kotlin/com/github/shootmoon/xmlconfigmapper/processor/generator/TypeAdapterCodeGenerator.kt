package com.github.shootmoon.xmlconfigmapper.processor.generator

import com.squareup.javapoet.*
import com.github.shootmoon.xmlconfigmapper.core.XmlConfigMapperContext
import com.github.shootmoon.xmlconfigmapper.core.adapter.ChildElementBinder
import com.github.shootmoon.xmlconfigmapper.core.adapter.TypeAdapter
import com.github.shootmoon.xmlconfigmapper.processor.field.AnnotatedClass
import org.dom4j.Element
import java.io.IOException
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/25 11:18
 */
class TypeAdapterCodeGenerator(private val filer: Filer, private val elements: Elements, private val types: Types)
{
    fun generateCode(annotatedClass: AnnotatedClass)
    {
        val genericParamTypeAdapter = ParameterizedTypeName.get(ClassName.get(TypeAdapter::class.java), ClassName.get(annotatedClass.element))
        val codeGenUtils = CodeGeneratorHelper(ClassName.get(annotatedClass.element))
        val constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)

        for ((xmlName, xmlElement) in annotatedClass.childElements)
        {
            constructorBuilder.addStatement("${CodeGeneratorHelper.childElementBindersParam}.put(\$S, \$L)", xmlName,
                    xmlElement.generateReadXmlCode(codeGenUtils))
        }

        val adapterClassBuilder = TypeSpec.classBuilder(annotatedClass.simpleClassName + TypeAdapter.GENERATED_CLASS_SUFFIX)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(genericParamTypeAdapter)

        generateFields(annotatedClass, adapterClassBuilder)

        adapterClassBuilder.addMethod(constructorBuilder.build()).addMethod(generateFromXmlMethod(annotatedClass).build())

        val packageElement = elements.getPackageOf(annotatedClass.element)
        val packageName = if (packageElement.isUnnamed) "" else packageElement.qualifiedName.toString()

        val javaFile = JavaFile.builder(packageName, adapterClassBuilder.build()).build()
        javaFile.writeTo(filer)
    }

    private fun generateFields(annotatedClass: AnnotatedClass, adapterClassBuilder: TypeSpec.Builder)
    {

        val targetClassToParseInto = ClassName.get(annotatedClass.element)

        if (annotatedClass.hasChildElements())
        {
            val childElementBinder = ParameterizedTypeName.get(ClassName.get(ChildElementBinder::class.java), targetClassToParseInto)
            val childElementBinderMapField = ParameterizedTypeName.get(ClassName.get(java.util.Map::class.java), ClassName.get(String::class.java), childElementBinder)
            val childElementBinderHashMapField = ParameterizedTypeName.get(ClassName.get(HashMap::class.java), ClassName.get(String::class.java), childElementBinder)

            adapterClassBuilder.addField(
                    FieldSpec.builder(childElementBinderMapField, CodeGeneratorHelper.childElementBindersParam, Modifier.PRIVATE)
                            .initializer("new \$T()", childElementBinderHashMapField)
                            .build())
        }

        // Add fields from TypeConverter
        for ((qualifiedConverterClass, fieldName) in CustomTypeConverterFieldNameManager.converterMap)
        {
            val converterClassName = ClassName.get(elements.getTypeElement(qualifiedConverterClass))
            adapterClassBuilder.addField(FieldSpec.builder(converterClassName, fieldName, Modifier.PRIVATE).initializer("new \$T()", converterClassName).build())
        }
    }

    private fun generateFromXmlMethod(annotatedClass: AnnotatedClass): MethodSpec.Builder
    {
        val targetClassToParseInto = ClassName.get(annotatedClass.element)

        val builder = MethodSpec.methodBuilder("fromXml")
                .returns(ClassName.get(annotatedClass.element))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .addParameter(Element::class.java, CodeGeneratorHelper.elementParam)
                .addParameter(XmlConfigMapperContext::class.java, CodeGeneratorHelper.contextParam)
                .addException(IOException::class.java)
                .addStatement("\$T \$L = new \$T()", targetClassToParseInto, CodeGeneratorHelper.valueParam, targetClassToParseInto)

        //
        // Read child elements
        //
        if (annotatedClass.hasChildElements())
        {
            builder.beginControlFlow("for(\$T childElementObj : \$L.elements())", Object::class.java, CodeGeneratorHelper.elementParam)
                    .addStatement("\$T childElement = (\$T)childElementObj", Element::class.java, Element::class.java)
                    .addStatement("\$T childElementBinder = \$L.get(childElement.getName())",
                            ParameterizedTypeName.get(ClassName.get(ChildElementBinder::class.java), targetClassToParseInto),
                            CodeGeneratorHelper.childElementBindersParam)
                    .beginControlFlow("if (childElementBinder != null)")
                    .addStatement("childElementBinder.fromXml(childElement, \$L, \$L)", CodeGeneratorHelper.valueParam, CodeGeneratorHelper.contextParam)
                    .endControlFlow() // end else skip remaining element
                    .endControlFlow() // End while

        }

        builder.addStatement("return \$L", CodeGeneratorHelper.valueParam)

        return builder;
    }
}