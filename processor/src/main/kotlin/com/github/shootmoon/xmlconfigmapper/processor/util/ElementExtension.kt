package com.github.shootmoon.xmlconfigmapper.processor.util

import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/24 15:15
 */


fun VariableElement.getSurroundingClass() =
        when (enclosingElement)
        {
            is TypeElement -> enclosingElement as TypeElement
            is ExecutableElement -> enclosingElement.enclosingElement as TypeElement
            else -> throw IllegalArgumentException("Unexpected enclosing element $enclosingElement for $this")
        }

fun VariableElement.getSurroundingClassQualifiedName() = getSurroundingClass().qualifiedName.toString()

fun Element.isList() = asType().toString().startsWith("java.util.List");

fun Element.isMethod() = kind == ElementKind.METHOD

fun Element.isField() = kind == ElementKind.FIELD

fun Element.isPublic() = modifiers.contains(Modifier.PUBLIC)

fun Element.isPrivate() = modifiers.contains(Modifier.PRIVATE)

fun Element.isProtected() = modifiers.contains(Modifier.PROTECTED)

fun Element.hasMinimumPackageVisibilityModifiers() = !isProtected() && !isPrivate()

fun Element.isMethodWithMinimumPackageVisibility() = isMethod() && hasMinimumPackageVisibilityModifiers()

fun Element.isConstructor() = kind == ElementKind.CONSTRUCTOR

fun Element.hasEmptyParameters()  =  (this as ExecutableElement).parameters.isEmpty()

fun Element.isEmptyConstructor() = isConstructor() && hasEmptyParameters()

fun Element.isEmptyConstructorWithMinimumPackageVisibility() = isEmptyConstructor() && hasMinimumPackageVisibilityModifiers()

fun Element.isGetterMethodWithMinimumPackageVisibility() = isMethodWithMinimumPackageVisibility() && (simpleName.startsWith("get") || simpleName.startsWith("is"))

fun Element.isSetterMethodWithMinimumPackageVisibility() = isMethodWithMinimumPackageVisibility() && simpleName.startsWith("set")

fun Element.isParameterlessMethod() = isMethod() && (this as ExecutableElement).parameters.isEmpty()

fun Element.isDefaultVisibility() = !isPrivate() && !isProtected() && !isPublic()

fun Element.isSamePackageAs(other: Element, utils: Elements) = utils.getPackageOf(this) == utils.getPackageOf(other)

fun Element.isMethodWithOneParameterOfType(type: TypeMirror, typeUtils: Types) = isMethod() && (this as ExecutableElement).parameters.size == 1 && typeUtils.isAssignable(parameters[0].asType(), type)