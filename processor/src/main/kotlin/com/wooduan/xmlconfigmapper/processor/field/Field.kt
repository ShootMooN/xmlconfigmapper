package com.wooduan.xmlconfigmapper.processor.field

import com.wooduan.xmlconfigmapper.processor.util.getSurroundingClassQualifiedName
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/24 11:25
 */
open class Field(val element: VariableElement, val name: String)
{
    open lateinit var accessResolver: FieldAccessResolver

    open val typeMirror: TypeMirror
        get() = element.asType()

    override fun toString() = "field '${element.simpleName}' in class ${element.getSurroundingClassQualifiedName()}"
}