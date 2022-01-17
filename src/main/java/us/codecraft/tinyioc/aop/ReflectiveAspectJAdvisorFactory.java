package us.codecraft.tinyioc.aop;

import org.aopalliance.aop.Advice;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import us.codecraft.tinyioc.beans.factory.AbstractBeanFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectiveAspectJAdvisorFactory implements AspectJAdvisorFactory {

    private final AbstractBeanFactory beanFactory;

    public ReflectiveAspectJAdvisorFactory(AbstractBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    @Override
    public boolean isAspect(Class<?> clazz) {
        return clazz.getDeclaredAnnotation(Aspect.class)!=null;
    }

    @Override
    public List<PointcutAdvisor> getAdvisors(BeanFactoryAspectInstanceFactory aspectInstanceFactory) {
        Class<?> aspectClass = aspectInstanceFactory.getResolvedType();
        String aspectName = aspectInstanceFactory.getName();

        List<PointcutAdvisor> advisors = new ArrayList<>();
        for (Method method : getAdvisorMethods(aspectClass)) {
            PointcutAdvisor advisor = getAdvisor(method, aspectInstanceFactory, advisors.size(), aspectName);
            if (advisor != null) {
                advisors.add(advisor);
            }
        }
        return advisors;
    }

    @Override
    public PointcutAdvisor getAdvisor(Method candidateAdviceMethod, BeanFactoryAspectInstanceFactory aspectInstanceFactory, int declarationOrderInAspect, String aspectName) {
        AspectJExpressionPointcut expressionPointcut = getPointcut(
                candidateAdviceMethod, aspectInstanceFactory.getResolvedType());
        if (expressionPointcut == null) {
            return null;
        }
       return new InstantiationModelAwarePointcutAdvisorImpl(expressionPointcut, candidateAdviceMethod,
                this, aspectInstanceFactory, declarationOrderInAspect, aspectName);
    }

    @Override
    public Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut, BeanFactoryAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName) {
        Class<?> candidateAspectClass = aspectInstanceFactory.getResolvedType();

        AbstractAspectJAdvisorFactory.AspectJAnnotation<?> aspectJAnnotation =
                AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
        if (aspectJAnnotation == null) {
            return null;
        }

        // If we get here, we know we have an AspectJ method.
        // Check that it's an AspectJ-annotated class
        if (!isAspect(candidateAspectClass)) {
            throw new RuntimeException("Advice must be declared inside an aspect type: " +
                    "Offending method '" + candidateAdviceMethod + "' in class [" +
                    candidateAspectClass.getName() + "]");
        }

        AbstractAspectJAdvice springAdvice=null;

        switch (aspectJAnnotation.getAnnotationType()) {
            case AtPointcut:
                return null;
            case AtAround:
                springAdvice = new AspectJAroundAdvice(
                        candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                break;
            case AtBefore:
                springAdvice = new AspectJMethodBeforeAdvice(
                        candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                break;
            case AtAfter:
                springAdvice = new AspectJAfterAdvice(
                        candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                break;
            case AtAfterReturning:
                /*springAdvice = new AspectJAfterReturningAdvice(
                        candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                AfterReturning afterReturningAnnotation = (AfterReturning) aspectJAnnotation.getAnnotation();
                if (StringUtils.hasText(afterReturningAnnotation.returning())) {
                    springAdvice.setReturningName(afterReturningAnnotation.returning());
                }*/
                break;
            case AtAfterThrowing:
                /*springAdvice = new AspectJAfterThrowingAdvice(
                        candidateAdviceMethod, expressionPointcut, aspectInstanceFactory);
                AfterThrowing afterThrowingAnnotation = (AfterThrowing) aspectJAnnotation.getAnnotation();
                if (StringUtils.hasText(afterThrowingAnnotation.throwing())) {
                    springAdvice.setThrowingName(afterThrowingAnnotation.throwing());
                }*/
                break;
            default:
                throw new UnsupportedOperationException(
                        "Unsupported advice type on method: " + candidateAdviceMethod);
        }

        // Now to configure the advice...
        springAdvice.setAspectName(aspectName);
        springAdvice.setDeclarationOrder(declarationOrder);
        /*String[] argNames = this.parameterNameDiscoverer.getParameterNames(candidateAdviceMethod);
        if (argNames != null) {
            springAdvice.setArgumentNamesFromStringArray(argNames);
        }*/
        springAdvice.calculateArgumentBindings();

        return springAdvice;
    }

    private AspectJExpressionPointcut getPointcut(Method candidateAdviceMethod, Class<?> candidateAspectClass) {
        AbstractAspectJAdvisorFactory.AspectJAnnotation<?> aspectJAnnotation =
                AbstractAspectJAdvisorFactory.findAspectJAnnotationOnMethod(candidateAdviceMethod);
        if (aspectJAnnotation == null) {
            return null;
        }

        AspectJExpressionPointcut ajexp =
                new AspectJExpressionPointcut(candidateAspectClass, new String[0], new Class<?>[0]);
        ajexp.setExpression(aspectJAnnotation.getPointcutExpression());
        if (this.beanFactory != null) {
            ajexp.setBeanFactory(this.beanFactory);
        }
        return ajexp;
    }



    private Iterable<? extends Method> getAdvisorMethods(Class<?> aspectClass) {
        return Arrays.stream(aspectClass.getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(Pointcut.class)==null)
                .collect(Collectors.toList());
    }
}
