package us.codecraft.tinyioc.aop;

import java.io.Serializable;
import java.lang.reflect.Method;

public class AspectJMethodBeforeAdvice extends AbstractAspectJAdvice implements MethodBeforeAdvice, Serializable {

    public AspectJMethodBeforeAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, pointcut, aspectInstanceFactory);
    }

    @Override
    public boolean isBeforeAdvice() {
        return true;
    }

    @Override
    public boolean isAfterAdvice() {
        return false;
    }

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        System.out.println("--------------AspectJMethodBeforeAdvice");
        invokeAdviceMethod(getJoinPointMatch(), null, null);
    }
}
