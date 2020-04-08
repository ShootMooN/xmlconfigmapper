package com.github.shootmoon.xmlconfigmapper.processor.field

import com.squareup.javapoet.CodeBlock
import com.github.shootmoon.xmlconfigmapper.processor.generator.CodeGeneratorHelper
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.VariableElement

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/23 21:07
 */
sealed class FieldAccessResolver
{
    abstract fun resolveSetter(assignValue: String, vararg arguments: Any): CodeBlock

    abstract fun resolveGetter(): String

    class GetterSetterFieldAccessResolver(private val getter: ExecutableElement, private val setter: ExecutableElement) : FieldAccessResolver()
    {
        override fun resolveSetter(assignValue: String, vararg arguments: Any) =
                CodeBlock.builder()
                        .addStatement("${CodeGeneratorHelper.valueParam}.${setter.simpleName}($assignValue)", *arguments)
                        .build()

        override fun resolveGetter() = "${CodeGeneratorHelper.valueParam}.${getter.simpleName}()"
    }

    class MinPackageVisibilityFieldAccessResolver(private val element: VariableElement) : FieldAccessResolver()
    {
        override fun resolveSetter(assignValue: String, vararg arguments: Any) =
                CodeBlock.builder()
                        .addStatement("${CodeGeneratorHelper.valueParam}.$element = $assignValue", *arguments)
                        .build()

        override fun resolveGetter() = "${CodeGeneratorHelper.valueParam}.$element"
    }
}