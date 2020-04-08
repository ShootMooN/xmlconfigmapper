package com.github.shootmoon.xmlconfigmapper.processor;

import com.github.shootmoon.xmlconfigmapper.core.annotation.XmlConfigMapping;
import com.github.shootmoon.xmlconfigmapper.processor.field.AnnotatedClass;
import com.github.shootmoon.xmlconfigmapper.processor.generator.TypeAdapterCodeGenerator;
import com.github.shootmoon.xmlconfigmapper.processor.scan.AnnotationDetector;
import com.github.shootmoon.xmlconfigmapper.processor.scan.AnnotationScanner;
import com.github.shootmoon.xmlconfigmapper.processor.field.AnnotatedClass;
import com.github.shootmoon.xmlconfigmapper.processor.generator.TypeAdapterCodeGenerator;
import com.github.shootmoon.xmlconfigmapper.processor.scan.AnnotationDetector;
import com.github.shootmoon.xmlconfigmapper.processor.scan.AnnotationScanner;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: longheng
 * @Description:
 * @Date: 2020/3/17 15:11
 */
public class MappingProcessor extends AbstractProcessor
{
    private Messager messager;
    private Filer filer;
    private Elements elements;
    private Types types;
    private AnnotationDetector annotationDetector;

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> types = new HashSet<>();
        types.add(XmlConfigMapping.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);

        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        elements = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        annotationDetector = new AnnotationDetector(elements, types);
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        try
        {
            AnnotationScanner scanner = new AnnotationScanner(annotationDetector);

            Set<? extends Element> configElements = roundEnv.getElementsAnnotatedWith(XmlConfigMapping.class);
            for (Element element : configElements)
            {
                if (element.getKind() != ElementKind.CLASS || element.getModifiers().contains(Modifier.ABSTRACT))
                {
                    continue;
                }

                AnnotatedClass clazz = new AnnotatedClass(element);
                scanner.scan(clazz);

                TypeAdapterCodeGenerator generator = new TypeAdapterCodeGenerator(filer, elements, types);
                generator.generateCode(clazz);
            }
        }
        catch (Exception e)
        {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }

        return false;
    }
}
