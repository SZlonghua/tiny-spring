package us.codecraft.tinyioc.aop;

import net.sf.cglib.core.CollectionUtils;
import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 代理相关的元数据
 * @author yihua.huang@dianping.com
 */
public class AdvisedSupport {

	private TargetSource targetSource;

    private MethodInterceptor methodInterceptor;

    private MethodMatcher methodMatcher;

    AdvisorChainFactory advisorChainFactory = new DefaultAdvisorChainFactory();

    private Advisor[] advisorArray = new Advisor[0];
    private List<Advisor> advisors = new ArrayList<>();

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    public MethodMatcher getMethodMatcher() {
        return methodMatcher;
    }

    public void setMethodMatcher(MethodMatcher methodMatcher) {
        this.methodMatcher = methodMatcher;
    }


    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) {
        return this.advisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice(
                this, method, targetClass);
    }


    public Advisor[] getAdvisors(){
        return this.advisorArray;
    }

    public void addAdvisors(Advisor... advisors) {
        addAdvisors(Arrays.asList(advisors));
    }

    /**
     * Add all of the given advisors to this proxy configuration.
     * @param advisors the advisors to register
     */
    public void addAdvisors(Collection<Advisor> advisors) {
        if (advisors != null && advisors.size() > 0) {
            for (Advisor advisor : advisors) {
                this.advisors.add(advisor);
            }
            updateAdvisorArray();
        }
    }
    protected final void updateAdvisorArray() {
        this.advisorArray = this.advisors.toArray(new Advisor[0]);
    }

}
