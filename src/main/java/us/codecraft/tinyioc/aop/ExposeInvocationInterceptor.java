package us.codecraft.tinyioc.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.io.Serializable;

public class ExposeInvocationInterceptor implements MethodInterceptor, Serializable, Ordered {


    public static final ExposeInvocationInterceptor INSTANCE = new ExposeInvocationInterceptor();

    private static final ThreadLocal<MethodInvocation> invocation = new ThreadLocal<MethodInvocation> ();

    public static final Advisor ADVISOR = new DefaultPointcutAdvisor(INSTANCE) {
        @Override
        public String toString() {
            return ExposeInvocationInterceptor.class.getName() +".ADVISOR";
        }
    };

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        MethodInvocation oldInvocation = invocation.get();
        invocation.set(mi);
        try {
            return mi.proceed();
        }
        finally {
            invocation.set(oldInvocation);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public static MethodInvocation currentInvocation() throws IllegalStateException {
        MethodInvocation mi = invocation.get();
        if (mi == null) {
            throw new IllegalStateException(
                    "No MethodInvocation found: Check that an AOP invocation is in progress and that the " +
                            "ExposeInvocationInterceptor is upfront in the interceptor chain. Specifically, note that " +
                            "advices with order HIGHEST_PRECEDENCE will execute before ExposeInvocationInterceptor! " +
                            "In addition, ExposeInvocationInterceptor and ExposeInvocationInterceptor.currentInvocation() " +
                            "must be invoked from the same thread.");
        }
        return mi;
    }
}
