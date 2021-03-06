package com.github.shootmoon.xmlconfigmapper.processor.generator

import java.util.HashMap

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/24 11:56
 */
object TypeConverterFieldNameManager
{
    val converterMap: Map<String, String> = HashMap()
    private var fieldNameCounter = 1

    fun getFieldNameForConverter(qualifiedConverterClassName: String) =
            (converterMap as MutableMap).getOrPut(qualifiedConverterClassName, { "typeConverter${fieldNameCounter++}" })
}