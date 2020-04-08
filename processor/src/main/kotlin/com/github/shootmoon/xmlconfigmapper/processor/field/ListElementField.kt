package com.github.shootmoon.xmlconfigmapper.processor.field

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeSpec
import com.github.shootmoon.xmlconfigmapper.processor.generator.CodeGeneratorHelper
import java.util.ArrayList
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/25 15:33
 */
class ListElementField(element: VariableElement, name: String, private val genericListType: TypeMirror) : ElementField(element, name)
{
    override fun generateReadXmlCode(codeGeneratorHelper: CodeGeneratorHelper): TypeSpec
    {

        val valueTypeAsArrayList = ParameterizedTypeName.get(ClassName.get(ArrayList::class.java), ClassName.get(genericListType))

        val fromXmlMethod = codeGeneratorHelper.fromXmlMethodBuilder()
                .addCode(CodeBlock.builder()
                        .beginControlFlow("if (${accessResolver.resolveGetter()} == null)")
                        .add(accessResolver.resolveSetter("new \$T()", valueTypeAsArrayList))
                        .endControlFlow()
                        .build())
                .addStatement("${accessResolver.resolveGetter()}.add((\$T)${CodeGeneratorHelper.contextParam}.getTypeAdapter(\$T.class).fromXml(${CodeGeneratorHelper.elementParam}, ${CodeGeneratorHelper.contextParam}))",
                        ClassName.get(genericListType), ClassName.get(genericListType))
                .build()

        return TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(codeGeneratorHelper.childElementBinderType)
                .addMethod(fromXmlMethod)
                .build()

    }
}