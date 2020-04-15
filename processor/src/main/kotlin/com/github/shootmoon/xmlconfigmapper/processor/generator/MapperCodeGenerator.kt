package com.github.shootmoon.xmlconfigmapper.processor.generator

import com.github.shootmoon.xmlconfigmapper.core.XmlConfigMapperContext
import com.github.shootmoon.xmlconfigmapper.core.annotation.XmlConfigBean
import com.github.shootmoon.xmlconfigmapper.core.annotation.XmlConfigMapping
import com.github.shootmoon.xmlconfigmapper.processor.util.ProcessingException
import com.github.shootmoon.xmlconfigmapper.processor.util.isList
import com.github.shootmoon.xmlconfigmapper.processor.util.isMethod
import com.github.shootmoon.xmlconfigmapper.processor.util.isString
import com.squareup.javapoet.*
import org.dom4j.Element
import org.dom4j.io.SAXReader
import java.io.File
import java.io.IOException
import java.lang.RuntimeException
import java.util.*
import javax.annotation.processing.Filer
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.WildcardType
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/4/13 21:54
 */
class MapperCodeGenerator(private val filer: Filer, private val elements: Elements, private val types: Types)
{
    fun generateCode(typeElement: TypeElement)
    {
        val mapperClassBuilder = TypeSpec.classBuilder(typeElement.simpleName.toString() + "\$\$Mapper")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(typeElement.asType())
                .addMethod(MethodSpec
                        .constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(XmlConfigMapperContext::class.java, CodeGeneratorHelper.contextParam)
                        .addStatement("this.${CodeGeneratorHelper.contextParam} = ${CodeGeneratorHelper.contextParam}")
                        .build())
                .addField(FieldSpec.builder(XmlConfigMapperContext::class.java, CodeGeneratorHelper.contextParam, Modifier.PRIVATE, Modifier.FINAL).build())

        typeElement.enclosedElements
                .filter { it.isMethod() && it.getAnnotation(XmlConfigMapping::class.java) != null }
                .filterIsInstance<ExecutableElement>()
                .forEach { generateMethods(it, mapperClassBuilder) }

        val packageElement = elements.getPackageOf(typeElement)
        val packageName = if (packageElement.isUnnamed) "" else packageElement.qualifiedName.toString()

        val javaFile = JavaFile.builder(packageName, mapperClassBuilder.build()).build()
        javaFile.writeTo(filer)
    }

    private fun generateMethods(executableElement: ExecutableElement, mapperClassBuilder: TypeSpec.Builder)
    {
        val returnElement = if(executableElement.returnType.isList()) types.asElement(getGenericTypeFromList(executableElement.returnType)) as TypeElement
                            else types.asElement(executableElement.returnType) as TypeElement

        if(returnElement.asType().kind != TypeKind.DECLARED || returnElement.getAnnotation(XmlConfigBean::class.java) == null)
        {
            throw ProcessingException(executableElement, "Return type of method ${executableElement.simpleName} " +
                    "should be a class with @XmlConfigBean annotation or a List which's generic type is a class with @XmlConfigBean annotation!")
        }

        if(executableElement.parameters.size != 1
                || (!executableElement.parameters[0].asType().isString() && executableElement.parameters[0].asType().toString() != "org.dom4j.Element"))
        {
            throw ProcessingException(executableElement, "Method ${executableElement.simpleName} should have only one parameter which is String or org.dom4j.Element!")
        }

        val parameterElement =  executableElement.parameters[0];
        val rootElementFieldName = parameterElement.simpleName.toString() + "RootElement"
        val valueTypeAsArrayList = ParameterizedTypeName.get(ClassName.get(ArrayList::class.java), ClassName.get(returnElement))
        val returnElementAnnotation = returnElement.getAnnotation(XmlConfigBean::class.java)
        val rootElementName =
                if (returnElementAnnotation == null || returnElementAnnotation.name.isEmpty())
                {
                    returnElement.simpleName.toString().decapitalize()
                }
                else
                {
                    returnElementAnnotation.name
                }


        val mapperMethodBuilder = MethodSpec.methodBuilder(executableElement.simpleName.toString())
                .returns(ClassName.get(executableElement.returnType))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override::class.java)
                .addParameter(ClassName.get(parameterElement.asType()), parameterElement.simpleName.toString())
                .beginControlFlow("try")
                .addStatement("\$T valueList = new \$T()", valueTypeAsArrayList, valueTypeAsArrayList)

        if(parameterElement.asType().isString())
        {
            mapperMethodBuilder
                    .addStatement("\$T inputXml = new \$T(\$L)", File::class.java, File::class.java, parameterElement.simpleName)
                    .addStatement("\$T saxReader = new \$T()", SAXReader::class.java, SAXReader::class.java)
                    .addStatement("\$T \$L = saxReader.read(inputXml).getRootElement()", Element::class.java, rootElementFieldName)

        }
        else
        {
            mapperMethodBuilder.addStatement("\$T \$L = \$L", Element::class.java, rootElementFieldName, parameterElement.simpleName)
        }

        mapperMethodBuilder
                .beginControlFlow("for(\$T childElementObj : \$L.elements(\$S))", Object::class.java, rootElementFieldName, rootElementName)
                .addStatement("\$T childElement = (\$T)childElementObj", Element::class.java, Element::class.java)
                .addStatement("valueList.add((\$T)${CodeGeneratorHelper.contextParam}.getTypeAdapter(\$T.class).fromXml(childElement, ${CodeGeneratorHelper.contextParam}))"
                        , ClassName.get(returnElement), ClassName.get(returnElement))
                .endControlFlow()

        if(executableElement.returnType.isList())
        {
            mapperMethodBuilder.addStatement("return valueList")
        }
        else
        {
            mapperMethodBuilder.addStatement("return valueList.size() == 0 ? null : valueList.get(0)")
        }

        mapperMethodBuilder
                .nextControlFlow("catch(\$T e)", Exception::class.java)
                .addStatement("throw new \$T(e)", RuntimeException::class.java)
                .endControlFlow()

        mapperClassBuilder.addMethod(mapperMethodBuilder.build());
    }

    private fun getGenericTypeFromList(listTypeMirror: TypeMirror): TypeMirror
    {
        val typeMirror = listTypeMirror as DeclaredType
        return when (typeMirror.typeArguments.size)
        {
            0    -> elements.getTypeElement("java.lang.Object").asType() // Raw types
            1    -> if (typeMirror.typeArguments[0].kind == TypeKind.WILDCARD)
                    {
                        val wildCardMirror = typeMirror.typeArguments[0] as WildcardType
                        when
                        {
                            wildCardMirror.extendsBound != null -> wildCardMirror.extendsBound
                            wildCardMirror.superBound != null   -> wildCardMirror.superBound
                            else                                -> elements.getTypeElement("java.lang.Object").asType()
                        }
                    }
                    else
                    {
                        typeMirror.typeArguments[0]
                    }
            else -> throw ProcessingException(null, "You have annotated a List with more than one generic argument!")
        }
    }
}