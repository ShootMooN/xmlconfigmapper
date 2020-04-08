package com.github.shootmoon.xmlconfigmapper.processor.scan

import com.github.shootmoon.xmlconfigmapper.processor.field.AnnotatedClass
import com.github.shootmoon.xmlconfigmapper.processor.field.Field
import com.github.shootmoon.xmlconfigmapper.processor.field.FieldAccessResolver
import com.github.shootmoon.xmlconfigmapper.processor.util.*
import com.github.shootmoon.xmlconfigmapper.processor.xml.XmlChildElement
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/19 15:15
 */
class AnnotationScanner(private val annotationDetector: AnnotationDetector)
{
    private val booleanFieldRegex = Regex("is[A-Z].*")

    fun scan(annotatedClass: AnnotatedClass)
    {
        val fieldWithMethodAccessRequired = ArrayList<Field>()
        val methodsMap = HashMap<String, ExecutableElement>()
        var constructorFound = false

        val checkAccessPolicyOrDeferGetterSetterCheck =
                fun(element: VariableElement, field: Field): Boolean
                {
                    if (element.hasMinimumPackageVisibilityModifiers())
                    {
                        field.accessResolver = FieldAccessResolver.MinPackageVisibilityFieldAccessResolver(field.element)
                        return true
                    }
                    else
                    {
                        fieldWithMethodAccessRequired.add(field)
                        return false
                    }
                }

        annotatedClass.element.enclosedElements.forEach()
        {
            if (it.isEmptyConstructorWithMinimumPackageVisibility())
            {
                constructorFound = true
            }
            else if (it.isGetterMethodWithMinimumPackageVisibility() || it.isSetterMethodWithMinimumPackageVisibility())
            {
                methodsMap[it.simpleName.toString()] = it as ExecutableElement
            }
            else if (it.isField())
            {
                fillAnnotatedClass(annotatedClass, it as VariableElement, checkAccessPolicyOrDeferGetterSetterCheck)
            }
        }

        if (!constructorFound)
        {
            throw ProcessingException(annotatedClass.element, "${annotatedClass.qualifiedClassName} " +
                    "must provide an empty (parameterless) constructor with minimum default (package) visibility")
        }

        fieldWithMethodAccessRequired.forEach()
        {
            val getter = checkGetter(it, methodsMap)
            val setter = checkSetter(it, methodsMap)

            it.accessResolver = FieldAccessResolver.GetterSetterFieldAccessResolver(getter, setter)
            addFieldToAnnotatedClass(annotatedClass, it)
        }
    }

    private inline fun fillAnnotatedClass(annotatedClass: AnnotatedClass, it: VariableElement, checkAccessPolicyOrDeferGetterSetterCheck: (VariableElement, Field) -> Boolean)
    {
        val field = annotationDetector.isXmlField(it)
        if (field != null)
        {
            // needs setter and getter?
            if (checkAccessPolicyOrDeferGetterSetterCheck(it, field))
            {
                addFieldToAnnotatedClass(annotatedClass, field)
            }
        }
    }

    private fun addFieldToAnnotatedClass(annotatedClass: AnnotatedClass, field: Field): Unit
    {
        when (field)
        {
            is XmlChildElement -> annotatedClass.addChildElement(field)

            else                                                                                     -> throw IllegalArgumentException(
                    "Oops, unexpected element type $field. This should never happen.")
        }
    }

    private fun checkGetter(field: Field, methodsMap: Map<String, ExecutableElement>): ExecutableElement
    {
        val element = field.element
        val elementName: String = element.simpleName.toString()
        val nameWithoutHungarian = getFieldNameWithoutHungarianNotation(element)

        var getter = findGetterForField(element, nameWithoutHungarian, "get", methodsMap)
                ?: findGetterForHungarianField(element, elementName, "get", methodsMap)
                ?: findGetterForHungarianFieldUpperCase(element, elementName, "get", methodsMap)

        // Test with "is" prefix
        if (getter == null && element.asType().isBoolean())
        {
            getter = findGetterForField(element, nameWithoutHungarian, "is", methodsMap)
                    ?: findGetterForHungarianField(element, elementName, "is", methodsMap)
                            ?: findGetterForHungarianFieldUpperCase(element, elementName, "is", methodsMap)
        }

        if (getter == null)
        {
            throw ProcessingException(element, "The field '${element.simpleName}' "
                    + "in class ${(element.enclosingElement as TypeElement).qualifiedName} "
                    + "has private or protected visibility. Hence a corresponding getter method must be provided "
                    + "with minimum package visibility (or public visibility if this is a super class in a different package) "
                    + "with the name ${bestMethodName(elementName, "get")}() or ${bestMethodName(elementName, "is")}() "
                    + "in case of a boolean. Unfortunately, there is no such getter method. Please provide one!")
        }

        if (!getter.isParameterlessMethod())
        {
            throw ProcessingException(element, "The getter method '$getter' for field '${element.simpleName}'"
                    + "in class ${element.getSurroundingClassQualifiedName()} "
                    + "must be parameterless (zero parameters).")
        }

        if (getter.isProtected() || getter.isPrivate() || (getter.isDefaultVisibility() && !getter.isSamePackageAs(element, annotationDetector.elements)))
        {
            throw ProcessingException(element, "The getter method '$getter' for field '${element.simpleName}' "
                    + "in class ${element.getSurroundingClassQualifiedName()} "
                    + "must have minimum package visibility (or public visibility if this is a super class in a different package)")
        }

        return getter
    }

    private fun checkSetter(field: Field, methodsMap: Map<String, ExecutableElement>): ExecutableElement
    {
        val element = field.element
        val elementName: String = element.simpleName.toString()
        val nameWithoutHungarian = getFieldNameWithoutHungarianNotation(element)

        // Setter method
        val setter = findMethodForField(nameWithoutHungarian, "set", methodsMap)
                ?: findMethodForHungarianField(elementName, "set", methodsMap)
                ?: findMethodForHungarianFieldUpperCase(elementName, "set", methodsMap)
                ?: throw ProcessingException(element, "The field '${element.simpleName}' "
                        + "in class ${(element.enclosingElement as TypeElement).qualifiedName} "
                        + "has private or protected visibility. Hence a corresponding setter method must be provided "
                        + "with the name ${bestMethodName(elementName, "set")}(${element.asType()}) and "
                        + "minimum package visibility (or public visibility if this is a super class in a different package). "
                        + "Unfortunately, there is no such setter method. Please provide one!")

        if (!setter.isMethodWithOneParameterOfType(element.asType(), annotationDetector.types))
        {
            throw ProcessingException(element, "The setter method '$setter' for field '${element.simpleName}' "
                    + "in class ${element.getSurroundingClassQualifiedName()} "
                    + "must have exactly one parameter of type '${element.asType()}'")
        }

        if (setter.isProtected() || setter.isPrivate() || (setter.isDefaultVisibility() && !setter.isSamePackageAs(element, annotationDetector.elements)))
        {
            throw ProcessingException(element, "The setter method '$setter' for field '${element.simpleName}' "
                    + "in class ${element.getSurroundingClassQualifiedName()} "
                    + "must have minimum package visibility (or public visibility if this is a super class in a different package)")
        }

        return setter
    }

    private fun getFieldNameWithoutHungarianNotation(element: VariableElement): String
    {
        val name = element.simpleName.toString()
        if (name.matches(Regex("^m[A-Z]{1}")))
        {
            return name.substring(1, 2).toLowerCase();
        }
        else if (name.matches(Regex("m[A-Z]{1}.*")))
        {
            return name.substring(1, 2).toLowerCase() + name.substring(2);
        }
        return name;
    }

    private fun findGetterForField(fieldElement: VariableElement, fieldName: String, methodNamePrefix: String,
                                   setterAndGetters: Map<String, ExecutableElement>): ExecutableElement?
    {

        val method = findMethodForField(fieldName, methodNamePrefix, setterAndGetters) ?: return null
        return if (annotationDetector.types.isSameType(method.returnType, fieldElement.asType())) method else null
    }

    private fun findMethodForField(fieldName: String, methodNamePrefix: String, setterAndGetters: Map<String, ExecutableElement>): ExecutableElement?
    {
        val methodName = bestMethodName(fieldName, methodNamePrefix)
        return setterAndGetters[methodName]
    }

    private fun bestMethodName(fieldName: String, methodNamePrefix: String): String
    {

        if (fieldName.length == 1)
        {
            // a should be getA()
            val builder = StringBuilder(methodNamePrefix)
            builder.append(fieldName.toUpperCase())
            return builder.toString()

        } /*else if (fieldName[0].isLowerCase() && fieldName[1].isUpperCase()) {
            // aString should be getaString()
            builder.append(fieldName)

        }*/
        else if (methodNamePrefix === "is" && fieldName.matches(booleanFieldRegex))
        {
            // field isFoo should be isFoo()
            return fieldName
        }
        else if (methodNamePrefix === "set" && fieldName.matches(booleanFieldRegex))
        {
            // field isFoo should be setFoo()
            val builder = StringBuilder(methodNamePrefix)
            builder.append(fieldName.substring(2))
            return builder.toString()
        }
        else
        {

            // foo should be getFoo()
            val builder = StringBuilder(methodNamePrefix)
            builder.append(Character.toUpperCase(fieldName[0]))
            builder.append(fieldName.substring(1))
            return builder.toString()
        }
    }

    private fun findGetterForHungarianField(fieldElement: VariableElement, fieldName: String, methodNamePrefix: String,
                                            setterAndGetters: Map<String, ExecutableElement>): ExecutableElement?
    {
        val method = findMethodForHungarianField(fieldName, methodNamePrefix, setterAndGetters) ?: return null
        return if (annotationDetector.types.isSameType(method.returnType, fieldElement.asType())) method else null
    }

    private fun findMethodForHungarianField(fieldName: String, methodNamePrefix: String,
                                            setterAndGetters: Map<String, ExecutableElement>): ExecutableElement?
    {

        // Search for setter method with hungarian notion check
        if (fieldName.length > 1 && fieldName.matches(Regex("m[A-Z].*")))
        {
            // m not in lower case
            val hungarianMethodName = methodNamePrefix + fieldName;
            return setterAndGetters[hungarianMethodName];
        }
        return null;
    }

    private fun findGetterForHungarianFieldUpperCase(fieldElement: VariableElement, fieldName: String, methodNamePrefix: String,
                                                     setterAndGetters: Map<String, ExecutableElement>): ExecutableElement?
    {

        val method = findMethodForHungarianFieldUpperCase(fieldName, methodNamePrefix, setterAndGetters) ?: return null
        return if (annotationDetector.types.isSameType(method.returnType, fieldElement.asType())) method else null
    }

    private fun findMethodForHungarianFieldUpperCase(fieldName: String, methodNamePrefix: String,
                                                     setterAndGetters: Map<String, ExecutableElement>): ExecutableElement?
    {

        // Search for setter method with hungarian notion check
        if (fieldName.length > 1 && fieldName.matches(Regex("m[A-Z].*")))
        {

            // M in upper case
            val hungarianMethodName = methodNamePrefix + Character.toUpperCase(fieldName[0]) + fieldName.substring(1)
            return setterAndGetters[hungarianMethodName]
        }

        return null
    }
}