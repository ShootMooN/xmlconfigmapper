package com.wooduan.xmlconfigmapper.processor.xml

import com.squareup.javapoet.TypeSpec
import com.wooduan.xmlconfigmapper.processor.generator.CodeGeneratorHelper

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/24 15:42
 */
interface XmlChildElement : XmlElement
{
    val name: String

    fun generateReadXmlCode(codeGeneratorHelper: CodeGeneratorHelper): TypeSpec
}