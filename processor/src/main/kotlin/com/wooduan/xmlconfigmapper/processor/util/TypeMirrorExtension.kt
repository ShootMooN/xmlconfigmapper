package com.wooduan.xmlconfigmapper.processor.util

import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/27 17:22
 */


fun TypeMirror.isBoolean() = kind == TypeKind.BOOLEAN || toString() == "java.lang.Boolean" || toString() == "kotlin.Boolean" || toString() == Boolean::class.qualifiedName || toString() == Boolean::class.java.canonicalName