package us.codecraft.tinyioc.aop;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.Interceptor;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultAdvisorChainFactory implements AdvisorChainFactory {
    @Override
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(AdvisedSupport config, Method method, Class<?> targetClass) {
        Advisor[] advisors = config.getAdvisors();
        List<Object> interceptorList = new ArrayList<>(advisors.length);
        Class<?> actualClass = (targetClass != null ? targetClass : method.getDeclaringClass());

        for (Advisor advisor : advisors) {
            if (advisor instanceof PointcutAdvisor) {
                PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
                if (pointcutAdvisor.getPointcut().getClassFilter().matches(actualClass)) {
                    MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();
                    if (mm.matches(method, actualClass)) {
                        MethodInterceptor[] interceptors = getInterceptors(advisor);
                        interceptorList.addAll(Arrays.asList(interceptors));
                    }
                }
            }
        }

        return interceptorList;
    }

    private MethodInterceptor[] getInterceptors(Advisor advisor) {
        List<MethodInterceptor> interceptors = new ArrayList<>();
        Advice advice = advisor.getAdvice();
        if(advice instanceof MethodInterceptor){
            interceptors.add((MethodInterceptor) advice);
        }
        if(advice instanceof MethodBeforeAdvice){
            MethodBeforeAdvice methodBeforeAdvice = (MethodBeforeAdvice) advice;
            interceptors.add(new MethodBeforeAdviceInterceptor(methodBeforeAdvice));
        }
        return interceptors.toArray(new MethodInterceptor[0]);
    }
}
