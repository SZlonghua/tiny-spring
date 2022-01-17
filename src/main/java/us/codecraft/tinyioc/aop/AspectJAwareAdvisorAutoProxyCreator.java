package us.codecraft.tinyioc.aop;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import us.codecraft.tinyioc.beans.BeanPostProcessor;
import us.codecraft.tinyioc.beans.factory.AbstractBeanFactory;
import us.codecraft.tinyioc.beans.factory.BeanFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yihua.huang@dianping.com
 */
public class AspectJAwareAdvisorAutoProxyCreator implements BeanPostProcessor, BeanFactoryAware {

	private AbstractBeanFactory beanFactory;

	private BeanFactoryAdvisorRetrievalHelper advisorRetrievalHelper;

	private BeanFactoryAspectJAdvisorsBuilder aspectJAdvisorsBuilder;

	private AspectJAdvisorFactory aspectJAdvisorFactory;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
		if (bean instanceof AspectJExpressionPointcutAdvisor) {
			return bean;
		}
		if (bean instanceof MethodInterceptor) {
			return bean;
		}
		/*List<AspectJExpressionPointcutAdvisor> advisors = beanFactory
				.getBeansForType(AspectJExpressionPointcutAdvisor.class);*/

		List<PointcutAdvisor> advisors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName);
		for (PointcutAdvisor advisor : advisors) {
			if (advisor.getPointcut().getClassFilter().matches(bean.getClass())) {
                ProxyFactory advisedSupport = new ProxyFactory();
				advisedSupport.setMethodInterceptor((MethodInterceptor) advisor.getAdvice());
				advisedSupport.setMethodMatcher(advisor.getPointcut().getMethodMatcher());

				TargetSource targetSource = new TargetSource(bean, bean.getClass(), bean.getClass().getInterfaces());
				advisedSupport.setTargetSource(targetSource);

				List<Advisor> collect = advisors.stream().map(a -> (Advisor) a).collect(Collectors.toList());
				advisedSupport.addAdvisors(collect);

				return advisedSupport.getProxy();
			}
		}
		return bean;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws Exception {
		this.beanFactory = (AbstractBeanFactory) beanFactory;
		initBeanFactory(this.beanFactory);
		if (this.aspectJAdvisorFactory == null) {
			this.aspectJAdvisorFactory = new ReflectiveAspectJAdvisorFactory(this.beanFactory);
		}
		this.aspectJAdvisorsBuilder =
				new BeanFactoryAspectJAdvisorsBuilderAdapter(this.beanFactory, this.aspectJAdvisorFactory);
	}

	protected void initBeanFactory(AbstractBeanFactory beanFactory) {
		this.advisorRetrievalHelper = new BeanFactoryAdvisorRetrievalHelperAdapter(beanFactory);
	}


	protected List<PointcutAdvisor> getAdvicesAndAdvisorsForBean(
			Class<?> beanClass, String beanName) {

		List<PointcutAdvisor> advisors = findEligibleAdvisors(beanClass, beanName);
		if (advisors.isEmpty()) {
			return new ArrayList();
		}
		return advisors;
	}

	protected List<PointcutAdvisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
		List<PointcutAdvisor> candidateAdvisors = findCandidateAdvisors();
		List<PointcutAdvisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);

		extendAdvisors(eligibleAdvisors);
		if (!eligibleAdvisors.isEmpty()) {
			eligibleAdvisors = sortAdvisors(eligibleAdvisors);
		}
		return eligibleAdvisors;
	}

	protected List<PointcutAdvisor> sortAdvisors(List<PointcutAdvisor> advisors) {
		if (advisors.size() > 1) {
			advisors.sort(new Comparator<PointcutAdvisor>() {
				@Override
				public int compare(PointcutAdvisor o1, PointcutAdvisor o2) {
					int compare = Integer.compare(getOrder(o1), getOrder(o2));
					if(compare!=0){
						return compare;
					}
					if(o1 instanceof MethodBeforeAdvice && !(o2 instanceof MethodBeforeAdvice)){
						return 1;
					}
					if(o2 instanceof MethodBeforeAdvice && !(o1 instanceof MethodBeforeAdvice)){
						return -1;
					}
					return 0;
				}
			});
		}
		return advisors;
	}

	private Integer getOrder(Object o){
		if(o instanceof Ordered){
			return ((Ordered) o).getOrder();
		}
		return null;
	}

	private void extendAdvisors(List<PointcutAdvisor> advisors) {
		if (!advisors.isEmpty() && !advisors.contains((PointcutAdvisor)ExposeInvocationInterceptor.ADVISOR)) {
			advisors.add(0, (PointcutAdvisor)ExposeInvocationInterceptor.ADVISOR);
		}
	}

	protected List<PointcutAdvisor> findAdvisorsThatCanApply(
			List<PointcutAdvisor> candidateAdvisors, Class<?> beanClass, String beanName) {
		List<PointcutAdvisor> advisors = new ArrayList<>();
		for(PointcutAdvisor advisor : candidateAdvisors){
			Pointcut pointcut = advisor.getPointcut();
			if(pointcut.getClassFilter().matches(beanClass) && hasMethodMatch(pointcut.getMethodMatcher(),beanClass)){
				advisors.add(advisor);
			}
		}
		return advisors;
	}

	private boolean hasMethodMatch(MethodMatcher methodMatcher, Class<?> beanClass) {
		return Arrays.stream(beanClass.getDeclaredMethods())
				.filter(m->methodMatcher.matches(m,beanClass))
				.count()>0;
	}

	protected List<PointcutAdvisor> findCandidateAdvisors() {
		List<PointcutAdvisor> advisors = this.advisorRetrievalHelper.findAdvisorBeans();
		if (this.aspectJAdvisorsBuilder != null) {
			advisors.addAll(this.aspectJAdvisorsBuilder.buildAspectJAdvisors());
		}
		return advisors;
	}


	private class BeanFactoryAdvisorRetrievalHelperAdapter extends BeanFactoryAdvisorRetrievalHelper {

		public BeanFactoryAdvisorRetrievalHelperAdapter(AbstractBeanFactory beanFactory) {
			super(beanFactory);
		}

	}

	private class BeanFactoryAspectJAdvisorsBuilderAdapter extends BeanFactoryAspectJAdvisorsBuilder {

		public BeanFactoryAspectJAdvisorsBuilderAdapter(
				AbstractBeanFactory beanFactory, AspectJAdvisorFactory advisorFactory) {

			super(beanFactory, advisorFactory);
		}


	}
}
