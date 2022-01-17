package us.codecraft.tinyioc.aop;

import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;
import java.util.List;

public interface AspectJAdvisorFactory {


    public boolean isAspect(Class<?> clazz);


    List<PointcutAdvisor> getAdvisors(BeanFactoryAspectInstanceFactory aspectInstanceFactory);


    PointcutAdvisor getAdvisor(Method candidateAdviceMethod, BeanFactoryAspectInstanceFactory aspectInstanceFactory,
                       int declarationOrder, String aspectName);


    Advice getAdvice(Method candidateAdviceMethod, AspectJExpressionPointcut expressionPointcut,
                     BeanFactoryAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName);
}
