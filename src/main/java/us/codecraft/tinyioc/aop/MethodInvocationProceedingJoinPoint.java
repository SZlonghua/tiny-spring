package us.codecraft.tinyioc.aop;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;

public class MethodInvocationProceedingJoinPoint implements ProceedingJoinPoint {

    private final MethodInvocation methodInvocation;

    public MethodInvocationProceedingJoinPoint(MethodInvocation methodInvocation) {
        this.methodInvocation = methodInvocation;
    }

    public MethodInvocation getMethodInvocation() {
        return methodInvocation;
    }

    @Override
    public void set$AroundClosure(AroundClosure arc) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object proceed() throws Throwable {
        return methodInvocation.proceed();
    }

    @Override
    public Object proceed(Object[] args) throws Throwable {
        ReflectiveMethodInvocation rmi = getRmi().clone();
        rmi.setArguments(args);
        return rmi.proceed();
    }

    @Override
    public String toShortString() {
        return "execution(" + getSignature().toShortString() + ")";
    }

    @Override
    public String toLongString() {
        return "execution(" + getSignature().toLongString() + ")";
    }

    private ReflectiveMethodInvocation getRmi(){
        return (ReflectiveMethodInvocation)this.methodInvocation;
    }

    @Override
    public Object getThis() {

        return getRmi().getProxy();
    }

    @Override
    public Object getTarget() {
        return getRmi().getTarget();
    }

    @Override
    public Object[] getArgs() {
        return getRmi().getArguments();
    }

    @Override
    public Signature getSignature() {
        return null;
    }

    @Override
    public SourceLocation getSourceLocation() {
        return null;
    }

    @Override
    public String getKind() {
        return null;
    }

    @Override
    public StaticPart getStaticPart() {
        return null;
    }
}
