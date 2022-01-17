package us.codecraft.tinyioc.aop;

import com.sun.deploy.util.StringUtils;
import org.aspectj.weaver.tools.*;
import us.codecraft.tinyioc.beans.factory.BeanFactory;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yihua.huang@dianping.com
 */
public class AspectJExpressionPointcut implements Pointcut, ClassFilter, MethodMatcher {

	private PointcutParser pointcutParser;

	private String expression;

	private PointcutExpression pointcutExpression;

	private Class<?> pointcutDeclarationScope;

	private String[] pointcutParameterNames = new String[0];

	private Class<?>[] pointcutParameterTypes = new Class<?>[0];

	private BeanFactory beanFactory;

	private static final Set<PointcutPrimitive> DEFAULT_SUPPORTED_PRIMITIVES = new HashSet<PointcutPrimitive>();

	static {
		DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
		DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.ARGS);
		DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
		DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.THIS);
		DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.TARGET);
		DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);
		DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
		DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
		DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
		DEFAULT_SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
	}

	public AspectJExpressionPointcut() {
		this(DEFAULT_SUPPORTED_PRIMITIVES);
	}

	public AspectJExpressionPointcut(Class<?> declarationScope, String[] paramNames, Class<?>[] paramTypes) {
		this(DEFAULT_SUPPORTED_PRIMITIVES);
		this.pointcutDeclarationScope = declarationScope;
		if (paramNames.length != paramTypes.length) {
			throw new IllegalStateException(
					"Number of pointcut parameter names must match number of pointcut parameter types");
		}
		this.pointcutParameterNames = paramNames;
		this.pointcutParameterTypes = paramTypes;
	}

	public AspectJExpressionPointcut(Set<PointcutPrimitive> supportedPrimitives) {
		pointcutParser = PointcutParser
				.getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(supportedPrimitives);
	}

	protected void checkReadyToMatch() {
		if (pointcutExpression == null) {
			pointcutExpression = buildPointcutExpression();
		}
	}

	private PointcutExpression buildPointcutExpression() {
		PointcutParameter[] pointcutParameters = new PointcutParameter[this.pointcutParameterNames.length];
		for (int i = 0; i < pointcutParameters.length; i++) {
			pointcutParameters[i] = pointcutParser.createPointcutParameter(
					this.pointcutParameterNames[i], this.pointcutParameterTypes[i]);
		}
		return pointcutParser.parsePointcutExpression(getExpression(),
				this.pointcutDeclarationScope, pointcutParameters);
	}


	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getExpression() {
		return expression;
	}

	@Override
	public ClassFilter getClassFilter() {
		return this;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return this;
	}

	@Override
	public boolean matches(Class targetClass) {
		checkReadyToMatch();
		return pointcutExpression.couldMatchJoinPointsInType(targetClass);
	}

	@Override
	public boolean matches(Method method, Class targetClass) {
		checkReadyToMatch();
		ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(method);
		if (shadowMatch.alwaysMatches()) {
			return true;
		} else if (shadowMatch.neverMatches()) {
			return false;
		}
		// TODO:其他情况不判断了！见org.springframework.aop.aspectj.RuntimeTestWalker
		return false;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
