package us.codecraft.tinyioc.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AnnotationUtils {

    public static Object getValue(Annotation annotation, String attributeName) {
        if (annotation == null || attributeName==null) {
            return null;
        }
        try {
            Method method = annotation.annotationType().getDeclaredMethod(attributeName);
            method.setAccessible(true);
            return method.invoke(annotation);
        }
        catch (NoSuchMethodException ex) {
            return null;
        }
        catch (InvocationTargetException ex) {
            throw new IllegalStateException("Could not obtain value for annotation attribute '" +
                    attributeName + "' in " + annotation, ex);
        }
        catch (Throwable ex) {
            return null;
        }
    }
}
