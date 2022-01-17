package us.codecraft.tinyioc.aop;

import net.sf.cglib.core.CollectionUtils;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.weaver.tools.JoinPointMatch;
import org.aspectj.weaver.tools.PointcutParameter;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public abstract class AbstractAspectJAdvice implements Advice, AspectJPrecedenceInformation, Serializable {

    private final Class<?> declaringClass;

    private final String methodName;

    private final Class<?>[] parameterTypes;

    protected transient Method aspectJAdviceMethod;

    private final AspectJExpressionPointcut pointcut;

    private final AspectInstanceFactory aspectInstanceFactory;


    private String aspectName = "";
    private int declarationOrder;


    private boolean argumentsIntrospected = false;
    private int joinPointArgumentIndex = -1;



    protected static final String JOIN_POINT_KEY = JoinPoint.class.getName();

    public AbstractAspectJAdvice(
            Method aspectJAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aspectInstanceFactory) {

        this.declaringClass = aspectJAdviceMethod.getDeclaringClass();
        this.methodName = aspectJAdviceMethod.getName();
        this.parameterTypes = aspectJAdviceMethod.getParameterTypes();
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.pointcut = pointcut;
        this.aspectInstanceFactory = aspectInstanceFactory;
    }

    @Override
    public String getAspectName() {
        return aspectName;
    }

    public void setAspectName(String aspectName) {
        this.aspectName = aspectName;
    }

    @Override
    public int getDeclarationOrder() {
        return declarationOrder;
    }

    public void setDeclarationOrder(int declarationOrder) {
        this.declarationOrder = declarationOrder;
    }


    public final synchronized void calculateArgumentBindings() {
        // The simple case... nothing to bind.
        if (this.argumentsIntrospected || this.parameterTypes.length == 0) {
            return;
        }

        int numUnboundArgs = this.parameterTypes.length;
        Class<?>[] parameterTypes = this.aspectJAdviceMethod.getParameterTypes();
        if (maybeBindJoinPoint(parameterTypes[0]) /*|| maybeBindProceedingJoinPoint(parameterTypes[0])*/) {
            numUnboundArgs--;
        }

        if (numUnboundArgs > 0) {
            // need to bind arguments by name as returned from the pointcut match
            //bindArgumentsByName(numUnboundArgs);
        }

        this.argumentsIntrospected = true;
    }

    private boolean maybeBindJoinPoint(Class<?> candidateParameterType) {
        if (JoinPoint.class == candidateParameterType) {
            this.joinPointArgumentIndex = 0;
            return true;
        }
        else {
            return false;
        }
    }

    protected JoinPointMatch getJoinPointMatch() {
        MethodInvocation mi = ExposeInvocationInterceptor.currentInvocation();
        /*if (!(mi instanceof ProxyMethodInvocation)) {
            throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
        }*/
        return getJoinPointMatch((ReflectiveMethodInvocation)mi);
    }

    protected JoinPointMatch getJoinPointMatch(ReflectiveMethodInvocation pmi) {
        String expression = this.pointcut.getExpression();
        return (expression != null ? (JoinPointMatch) pmi.getUserAttribute(expression) : null);
    }

    protected Object invokeAdviceMethod(
            JoinPointMatch jpMatch, Object returnValue, Throwable ex)
            throws Throwable {

        return invokeAdviceMethodWithGivenArgs(argBinding(getJoinPoint(), jpMatch, returnValue, ex));
    }

    protected Object invokeAdviceMethodWithGivenArgs(Object[] args) throws Throwable {
        Object[] actualArgs = args;
        if (this.aspectJAdviceMethod.getParameterCount() == 0) {
            actualArgs = null;
        }
        try {
            this.aspectJAdviceMethod.setAccessible(true);
            // TODO AopUtils.invokeJoinpointUsingReflection
            return this.aspectJAdviceMethod.invoke(this.aspectInstanceFactory.getAspectInstance(), actualArgs);
        }
        catch (IllegalArgumentException ex) {
            throw new RuntimeException("Mismatch on arguments to advice method [" +
                    this.aspectJAdviceMethod + "]; pointcut expression [" +
                    this.pointcut.getExpression() + "]", ex);
        }
        catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }

    protected JoinPoint getJoinPoint() {
        return currentJoinPoint();
    }

    public static JoinPoint currentJoinPoint() {
        MethodInvocation mi = ExposeInvocationInterceptor.currentInvocation();

        ReflectiveMethodInvocation pmi = (ReflectiveMethodInvocation) mi;
        JoinPoint jp = (JoinPoint) pmi.getUserAttribute(JOIN_POINT_KEY);
        if (jp == null) {
            jp = new MethodInvocationProceedingJoinPoint(pmi);
            pmi.setUserAttribute(JOIN_POINT_KEY, jp);
        }
        return jp;
    }

    protected Object[] argBinding(JoinPoint jp, JoinPointMatch jpMatch,
                                  Object returnValue, Throwable ex) {

        calculateArgumentBindings();

        // AMC start
        Object[] adviceInvocationArgs = new Object[this.parameterTypes.length];
        int numBound = 0;

        if (this.joinPointArgumentIndex != -1) {
            adviceInvocationArgs[this.joinPointArgumentIndex] = jp;
            numBound++;
        }



        if (numBound != this.parameterTypes.length) {
            throw new IllegalStateException("Required to bind " + this.parameterTypes.length +
                    " arguments, but only bound " + numBound + " (JoinPointMatch " +
                    (jpMatch == null ? "was NOT" : "WAS") + " bound in invocation)");
        }

        return adviceInvocationArgs;
    }
}
