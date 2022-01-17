package us.codecraft.tinyioc.aop;

import org.aopalliance.aop.Advice;

/**
 * @author yihua.huang@dianping.com
 */
public class AspectJExpressionPointcutAdvisor implements PointcutAdvisor,Ordered {

    private AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();

    private Advice advice;

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    public void setExpression(String expression) {
        this.pointcut.setExpression(expression);
    }

	@Override
	public Advice getAdvice() {
		return advice;
	}

    @Override
	public Pointcut getPointcut() {
		return pointcut;
	}

    @Override
    public int getOrder() {
        Advice advice = getAdvice();
        if (advice instanceof Ordered) {
            return ((Ordered) advice).getOrder();
        }
        return Ordered.LOWEST_PRECEDENCE;
    }
}
