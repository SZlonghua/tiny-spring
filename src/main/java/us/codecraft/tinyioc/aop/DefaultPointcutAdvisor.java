package us.codecraft.tinyioc.aop;

import org.aopalliance.aop.Advice;

public class DefaultPointcutAdvisor implements PointcutAdvisor,Ordered {

    private Advice advice = new Advice() {};

    private Pointcut pointcut = new Pointcut(){

        @Override
        public ClassFilter getClassFilter() {
            return (classFilter)->true;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return (m,t)->true;
        }
    };

    public DefaultPointcutAdvisor(Advice advice) {
        this.advice = advice;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
