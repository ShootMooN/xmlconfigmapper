package com.github.shootmoon.xmlconfigmapper.processor.util

import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/27 17:22
 */


fun TypeMirror.isBoolean() = toString() == Boolean::class.java.canonicalName

fun TypeMirror.isString() = toString() == String::class.java.canonicalName

fun TypeMirror.isList() = toString().startsWith("java.util.List");