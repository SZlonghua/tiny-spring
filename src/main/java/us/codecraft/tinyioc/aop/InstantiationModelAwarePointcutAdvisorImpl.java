package us.codecraft.tinyioc.aop;

import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;

public class InstantiationModelAwarePointcutAdvisorImpl implements InstantiationModelAwarePointcutAdvisor,Ordered {

    private static final Advice EMPTY_ADVICE = new Advice() {};


    private final AspectJExpressionPointcut declaredPointcut;

    private final Class<?> declaringClass;

    private final String methodName;

    private final Class<?>[] parameterTypes;

    private transient Method aspectJAdviceMethod;

    private final AspectJAdvisorFactory aspectJAdvisorFactory;

    private final BeanFactoryAspectInstanceFactory aspectInstanceFactory;

    private final int declarationOrder;

    private final String aspectName;

    private final Pointcut pointcut;


    private Advice instantiatedAdvice;

   /* private Boolean isBeforeAdvice;

    private Boolean isAfterAdvice;*/

    public InstantiationModelAwarePointcutAdvisorImpl(AspectJExpressionPointcut declaredPointcut,
                                                      Method aspectJAdviceMethod, AspectJAdvisorFactory aspectJAdvisorFactory,
                                                      BeanFactoryAspectInstanceFactory aspectInstanceFactory, int declarationOrder, String aspectName) {

        this.declaredPointcut = declaredPointcut;
        this.declaringClass = aspectJAdviceMethod.getDeclaringClass();
        this.methodName = aspectJAdviceMethod.getName();
        this.parameterTypes = aspectJAdviceMethod.getParameterTypes();
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.aspectJAdvisorFactory = aspectJAdvisorFactory;
        this.aspectInstanceFactory = aspectInstanceFactory;
        this.declarationOrder = declarationOrder;
        this.aspectName = aspectName;

        this.pointcut = this.declaredPointcut;
        this.instantiatedAdvice = instantiateAdvice(this.declaredPointcut);
    }
    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        if (this.instantiatedAdvice == null) {
            this.instantiatedAdvice = instantiateAdvice(this.declaredPointcut);
        }
        return this.instantiatedAdvice;
    }

    private Advice instantiateAdvice(AspectJExpressionPointcut pointcut) {
        Advice advice = this.aspectJAdvisorFactory.getAdvice(this.aspectJAdviceMethod, pointcut,
                this.aspectInstanceFactory, this.declarationOrder, this.aspectName);
        return (advice != null ? advice : EMPTY_ADVICE);
    }

    @Override
    public int getOrder() {
        return 1000;
    }
}
