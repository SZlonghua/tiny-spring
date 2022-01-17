package us.codecraft.tinyioc.aop;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * 基于jdk的动态代理
 *
 * @author yihua.huang@dianping.com
 */
public class JdkDynamicAopProxy extends AbstractAopProxy implements InvocationHandler {

    public JdkDynamicAopProxy(AdvisedSupport advised) {
        super(advised);
    }

	@Override
	public Object getProxy() {
		return Proxy.newProxyInstance(getClass().getClassLoader(), advised.getTargetSource().getInterfaces(), this);
	}

	/*@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		MethodInterceptor methodInterceptor = advised.getMethodInterceptor();
		if (advised.getMethodMatcher() != null
				&& advised.getMethodMatcher().matches(method, advised.getTargetSource().getTarget().getClass())) {
			return methodInterceptor.invoke(new ReflectiveMethodInvocation(advised.getTargetSource().getTarget(),
					method, args));
		} else {
			return method.invoke(advised.getTargetSource().getTarget(), args);
		}
	}*/

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, advised.getTargetSource().getTargetClass());
		if (chain.isEmpty()) {
			// We can skip creating a MethodInvocation: just invoke the target directly
			// Note that the final invoker must be an InvokerInterceptor so we know it does
			// nothing but a reflective operation on the target, and no hot swapping or fancy proxying.
			return method.invoke(advised.getTargetSource().getTarget(), args);
		}
		else {
			// We need to create a method invocation...
			MethodInvocation invocation =
					new ReflectiveMethodInvocation(proxy, advised.getTargetSource().getTarget(), method, args, advised.getTargetSource().getTargetClass(), chain);
			// Proceed to the joinpoint through the interceptor chain.
			return invocation.proceed();
		}
	}

}
