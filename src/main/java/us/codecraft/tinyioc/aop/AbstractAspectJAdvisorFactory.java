package us.codecraft.tinyioc.aop;

import org.aspectj.lang.annotation.*;
import org.aspectj.lang.annotation.Pointcut;
import us.codecraft.tinyioc.util.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAspectJAdvisorFactory implements AspectJAdvisorFactory {

    private static final Class<?>[] ASPECTJ_ANNOTATION_CLASSES = new Class<?>[] {
            Pointcut.class, Around.class, Before.class, After.class, AfterReturning.class, AfterThrowing.class};

    protected static AspectJAnnotation<?> findAspectJAnnotationOnMethod(Method method) {
        for (Class<?> clazz : ASPECTJ_ANNOTATION_CLASSES) {
            AspectJAnnotation<?> foundAnnotation = findAnnotation(method, (Class<Annotation>) clazz);
            if (foundAnnotation != null) {
                return foundAnnotation;
            }
        }
        return null;
    }


    private static <A extends Annotation> AspectJAnnotation<A> findAnnotation(Method method, Class<A> annotationType) {
        if (annotationType == null) {
            return null;
        }
        A result = method.getDeclaredAnnotation(annotationType);
        if (result != null) {
            return new AspectJAnnotation<>(result);
        }
        else {
            return null;
        }
    }


    protected static class AspectJAnnotation<A extends Annotation> {

        private static final String[] EXPRESSION_ATTRIBUTES = new String[] {"pointcut", "value"};

        private static Map<Class<?>, AspectJAnnotationType> annotationTypeMap = new HashMap(8);

        static {
            annotationTypeMap.put(Pointcut.class, AspectJAnnotationType.AtPointcut);
            annotationTypeMap.put(Around.class, AspectJAnnotationType.AtAround);
            annotationTypeMap.put(Before.class, AspectJAnnotationType.AtBefore);
            annotationTypeMap.put(After.class, AspectJAnnotationType.AtAfter);
            annotationTypeMap.put(AfterReturning.class, AspectJAnnotationType.AtAfterReturning);
            annotationTypeMap.put(AfterThrowing.class, AspectJAnnotationType.AtAfterThrowing);
        }

        private final A annotation;

        private final AspectJAnnotationType annotationType;

        private final String pointcutExpression;

        private final String argumentNames;

        public AspectJAnnotation(A annotation) {
            this.annotation = annotation;
            this.annotationType = determineAnnotationType(annotation);
            try {
                this.pointcutExpression = resolveExpression(annotation);
                Object argNames = AnnotationUtils.getValue(annotation, "argNames");
                this.argumentNames = (argNames instanceof String ? (String) argNames : "");
            }
            catch (Exception ex) {
                throw new IllegalArgumentException(annotation + " is not a valid AspectJ annotation", ex);
            }
        }

        private AspectJAnnotationType determineAnnotationType(A annotation) {
            AspectJAnnotationType type = annotationTypeMap.get(annotation.annotationType());
            if (type != null) {
                return type;
            }
            throw new IllegalStateException("Unknown annotation type: " + annotation);
        }

        private String resolveExpression(A annotation) {
            for (String attributeName : EXPRESSION_ATTRIBUTES) {
                Object val = AnnotationUtils.getValue(annotation, attributeName);
                if (val instanceof String) {
                    String str = (String) val;
                    if (!str.isEmpty()) {
                        return str;
                    }
                }
            }
            throw new IllegalStateException("Failed to resolve expression: " + annotation);
        }

        public AspectJAnnotationType getAnnotationType() {
            return this.annotationType;
        }

        public A getAnnotation() {
            return this.annotation;
        }

        public String getPointcutExpression() {
            return this.pointcutExpression;
        }

        public String getArgumentNames() {
            return this.argumentNames;
        }

        @Override
        public String toString() {
            return this.annotation.toString();
        }
    }

    protected enum AspectJAnnotationType {

        AtPointcut, AtAround, AtBefore, AtAfter, AtAfterReturning, AtAfterThrowing
    }
}
