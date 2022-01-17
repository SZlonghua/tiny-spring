package us.codecraft.tinyioc.aop;

/**
 * @author yihua.huang@dianping.com
 */
public class ProxyFactory extends AdvisedSupport implements AopProxy {

	@Override
	public Object getProxy() {
		return createAopProxy().getProxy();
	}

	protected final AopProxy createAopProxy() {
		if(this.getTargetSource().getInterfaces().length > 0 && instanceOf(getTargetSource().getTargetClass(),getTargetSource().getInterfaces()) ){
			return new JdkDynamicAopProxy(this);
		}
		return new Cglib2AopProxy(this);
	}

	private boolean instanceOf(Class targetClass ,Class<?>[] interfaces){
		for (Class clazz : interfaces) {
		    if (clazz.isAssignableFrom(targetClass)){
		    	return true;
			}
		}
		return false;
	}
}
