package com.github.shootmoon.xmlconfigmapper.processor;

import com.github.shootmoon.xmlconfigmapper.core.annotation.XmlConfigBean;
import com.github.shootmoon.xmlconfigmapper.core.annotation.XmlConfigMapper;
import com.github.shootmoon.xmlconfigmapper.processor.field.AnnotatedClass;
import com.github.shootmoon.xmlconfigmapper.processor.generator.MapperCodeGenerator;
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
        types.add(XmlConfigMapper.class.getCanonicalName());
        types.add(XmlConfigBean.class.getCanonicalName());
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
            //Generate type adaptor
            AnnotationScanner annotationScanner = new AnnotationScanner(annotationDetector);
            TypeAdapterCodeGenerator typeAdapterCodeGenerator = new  TypeAdapterCodeGenerator(filer, elements, types);
            Set<? extends Element> configBeanElements = roundEnv.getElementsAnnotatedWith(XmlConfigBean.class);
            for (Element configBeanElement : configBeanElements)
            {
                if (configBeanElement.getKind() != ElementKind.CLASS || configBeanElement.getModifiers().contains(Modifier.ABSTRACT))
                {
                    continue;
                }

                AnnotatedClass clazz = new AnnotatedClass((TypeElement) configBeanElement);
                annotationScanner.scan(clazz);
                typeAdapterCodeGenerator.generateCode(clazz);
            }

            //Generate mapper implement class
            MapperCodeGenerator mapperCodeGenerator = new MapperCodeGenerator(filer, elements, types);
            Set<? extends Element> configElements = roundEnv.getElementsAnnotatedWith(XmlConfigMapper.class);
            for (Element element : configElements)
            {
                if (element.getKind() != ElementKind.INTERFACE)
                {
                    continue;
                }

                mapperCodeGenerator.generateCode((TypeElement)element);
            }
        }
        catch (Exception e)
        {
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }

        return false;
    }
}
