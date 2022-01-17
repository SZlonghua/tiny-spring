package us.codecraft.tinyioc.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.io.Serializable;
import java.lang.reflect.Method;

public class AspectJAfterAdvice extends AbstractAspectJAdvice implements MethodInterceptor, Serializable,Ordered {

    public AspectJAfterAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, pointcut, aspectInstanceFactory);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {

        System.out.println("---------------AspectJAfterAdvice");
        try {
            return mi.proceed();
        }
        finally {
            invokeAdviceMethod(getJoinPointMatch(), null, null);
        }
    }

    @Override
    public boolean isBeforeAdvice() {
        return false;
    }

    @Override
    public boolean isAfterAdvice() {
        return true;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
