package com.wooduan.xmlconfigmapper.processor.scan

import com.wooduan.xmlconfigmapper.core.annotation.Ignore
import com.wooduan.xmlconfigmapper.core.annotation.Property
import com.wooduan.xmlconfigmapper.core.converter.TypeConverter
import com.wooduan.xmlconfigmapper.processor.field.Field
import com.wooduan.xmlconfigmapper.processor.field.ListElementField
import com.wooduan.xmlconfigmapper.processor.field.PropertyField
import com.wooduan.xmlconfigmapper.processor.util.ProcessingException
import com.wooduan.xmlconfigmapper.processor.util.isList
import java.lang.reflect.Modifier
import javax.lang.model.element.*
import javax.lang.model.type.*
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import kotlin.collections.HashSet

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/19 14:52
 */
class AnnotationDetector(val elements: Elements, val types: Types)
{
    private var converterTypeNamesSet: HashSet<String>? = null

    fun isXmlField(element: VariableElement): Field?
    {
        if (element.getAnnotation(Ignore::class.java) != null)
        {
            return null
        }

        val propertyAnnotation = element.getAnnotation(Property::class.java)
        if (propertyAnnotation != null)
        {
            return PropertyField(element,
                    nameFromPropertyAnnotationOrField(propertyAnnotation, element),
                    getQualifiedConverterName(element, propertyAnnotation))
        }

        if (containsTypeConverter(element.asType().toString()))
        {
            return PropertyField(element, element.simpleName.toString())
        }

        //ChildElement mush in List
        if (element.isList())
        {
            val genericListType = getGenericTypeFromList(element)
            val genericListTypeElement = types.asElement(genericListType) as TypeElement

            return ListElementField(
                    element,
                    genericListTypeElement.simpleName.toString().decapitalize(),
                    genericListType)

        }

        throw ProcessingException(element, "${element.enclosingElement.simpleName}.${element.simpleName} is not supported, you can" +
                " set custom type converter by @${Property::class.simpleName}, or use @${Ignore::class.simpleName} to skip.")
    }

    private fun nameFromPropertyAnnotationOrField(property: Property, element: VariableElement) =
            if (property.name.isBlank())
            {
                element.simpleName.toString()
            }
            else
            {
                property.name
            }

    private fun getQualifiedConverterName(element: Element, annotation: Property): String?
    {
        try
        {
            val converterClass = annotation.converter.java

            // No type converter
            if (converterClass == TypeConverter.NoneTypeConverter::class.java)
            {
                null
            }

            // Class must be public
            if (!Modifier.isPublic(converterClass.modifiers))
            {
                throw ProcessingException(element, "TypeConverter class ${converterClass.canonicalName} must be a public class!")
            }

            if (Modifier.isAbstract(converterClass.modifiers))
            {
                throw ProcessingException(element, "TypeConverter class ${converterClass.canonicalName} cannot be a abstract")
            }

            if (Modifier.isInterface(converterClass.modifiers))
            {
                throw ProcessingException(element, "TypeConverter class ${converterClass.canonicalName} cannot be an interface. Only classes are allowed!")
            }

            // Must have default constructor
            val constructors = converterClass.constructors
            for (c in constructors)
            {
                val isPublicConstructor = Modifier.isPublic(c.modifiers);
                val paramTypes = c.parameterTypes;

                if (paramTypes.isEmpty() && isPublicConstructor)
                {
                    return converterClass.canonicalName
                }
            }

            // No public constructor found
            throw ProcessingException(element, "TypeConverter class ${converterClass.canonicalName} must provide an empty (parameter-less) public constructor")
        }
        catch (mte: MirroredTypeException)
        {
            // Not compiled class
            val typeMirror = mte.typeMirror

            if (typeMirror.toString() == TypeConverter.NoneTypeConverter::class.qualifiedName)
            {
                return null
            }

            if (typeMirror.kind != TypeKind.DECLARED)
            {
                throw ProcessingException(element, "TypeConverter must be a class")
            }

            val typeConverterType = typeMirror as DeclaredType
            val typeConverterElement = typeConverterType.asElement()

            if (typeConverterElement.kind != ElementKind.CLASS)
            {
                throw ProcessingException(element, "TypeConverter ${typeConverterElement} must be a public class!")
            }

            if (!typeConverterElement.modifiers.contains(javax.lang.model.element.Modifier.PUBLIC))
            {
                throw ProcessingException(element, "TypeConverter ${typeConverterElement} class is not public!")
            }

            // Check empty constructor
            for (e in (typeConverterElement as TypeElement).enclosedElements)
            {
                if (e.kind == ElementKind.CONSTRUCTOR)
                {
                    val constructor = e as ExecutableElement
                    if (constructor.modifiers.contains(javax.lang.model.element.Modifier.PUBLIC)
                            && constructor.parameters.isEmpty())
                    {
                        return typeMirror.toString()
                    }
                }
            }

            throw ProcessingException(element, "TypeConverter class ${typeMirror} must provide an empty (parameter-less) public constructor")
        }
    }

    private fun containsTypeConverter(typeString: String): Boolean
    {
        if (converterTypeNamesSet == null)
        {
            converterTypeNamesSet = elements.getPackageElement(TypeConverter::class.java.`package`.name)
                    .enclosedElements.filterIsInstance<TypeElement>()
                    .flatMap { it.interfaces }
                    .filterIsInstance<DeclaredType>()
                    .filter { (it.asElement() as TypeElement).qualifiedName.toString().equals(TypeConverter::class.qualifiedName) }
                    .map { it.typeArguments[0].toString() }
                    .toHashSet()
        }

        return converterTypeNamesSet?.contains(typeString) ?: false
    }

    private fun getGenericTypeFromList(listVariableElement: VariableElement): TypeMirror
    {
        if (listVariableElement.asType().kind != TypeKind.DECLARED)
        {
            throw ProcessingException(listVariableElement, "Element must be of type java.util.List")
        }

        val typeMirror = listVariableElement.asType() as DeclaredType
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
            else -> throw ProcessingException(listVariableElement, "You have annotated a List with more than one generic argument!")
        }
    }
}