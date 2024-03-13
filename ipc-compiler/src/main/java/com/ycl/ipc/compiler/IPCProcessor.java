package com.ycl.ipc.compiler;

import static com.ycl.ipc.compiler.Consts.PARCELABLE;
import static com.ycl.ipc.compiler.Consts.SERIALIZABLE;
import static javax.lang.model.element.ElementKind.INTERFACE;

import com.google.auto.service.AutoService;
import com.ycl.ipc.annotation.IPCInterface;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public class IPCProcessor extends AbstractProcessor {


    Elements elementUtils;

    Types types;

    Logger logger;

    private TypeMirror parcelableType;
    private TypeMirror serializableType;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        logger = new Logger(processingEnv.getMessager());
        parcelableType = processingEnv.getElementUtils().getTypeElement(PARCELABLE).asType();
        serializableType = processingEnv.getElementUtils().getTypeElement(SERIALIZABLE).asType();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (roundEnvironment.processingOver()) {
            return false;
        }
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(IPCInterface.class);
        for (Element element : elements) {
            for (Element enclosedElement : element.getEnclosedElements()) {
                if (enclosedElement.getKind().equals(ElementKind.METHOD)) {
                    ExecutableElement executableElement = (ExecutableElement) enclosedElement;
                    for (VariableElement parameter : executableElement.getParameters()) {
                        boolean f;
                        if (!(f = isTypeValid(parameter))) {
                            logger.error("parameter type is not support!!! at \nclass: " + element.getSimpleName() + "\nmethod: " + executableElement.getSimpleName() + "\nparameter: " + parameter.asType() + " " + parameter.getSimpleName());
                        }
                        logger.info(executableElement + ",parameter:" + parameter.getKind() + "res " + f);
                    }
                }
            }
        }
        return false;
    }


    private boolean isTypeValid(Element element) {
        if (element.asType().getKind().isPrimitive()) {
            return true;
        }
        if (isInterface(element.asType())) {
            return true;
        }
        if (types.isSubtype(element.asType(), parcelableType)) {
            return true;
        }
        if (types.isSubtype(element.asType(), serializableType)) {
            return true;
        }
        return false;
    }

    private boolean isInterface(TypeMirror typeMirror) {
        return typeMirror instanceof DeclaredType
                && ((DeclaredType) typeMirror).asElement().getKind() == INTERFACE;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(IPCInterface.class.getCanonicalName());
        return set;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
