package us.codecraft.tinyioc.aop;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import us.codecraft.tinyioc.beans.factory.BeanFactory;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author yihua.huang@dianping.com
 */
public class AspectJAroundAdvice extends AbstractAspectJAdvice implements Serializable, MethodInterceptor, Ordered {

	private BeanFactory beanFactory;

	private Method aspectJAdviceMethod;

	private String aspectInstanceName;

    public AspectJAroundAdvice(Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aspectInstanceFactory) {
        super(aspectJAdviceMethod, pointcut, aspectInstanceFactory);
    }

    @Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("---------------AspectJAroundAdvice");
        return aspectJAdviceMethod.invoke(beanFactory.getBean(aspectInstanceName), invocation);
	}

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Method getAspectJAdviceMethod() {
        return aspectJAdviceMethod;
    }

    public void setAspectJAdviceMethod(Method aspectJAdviceMethod) {
        this.aspectJAdviceMethod = aspectJAdviceMethod;
    }

    public String getAspectInstanceName() {
        return aspectInstanceName;
    }

    public void setAspectInstanceName(String aspectInstanceName) {
        this.aspectInstanceName = aspectInstanceName;
    }

    @Override
    public boolean isBeforeAdvice() {
        return false;
    }

    @Override
    public boolean isAfterAdvice() {
        return false;
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
