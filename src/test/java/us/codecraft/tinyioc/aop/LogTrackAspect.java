package us.codecraft.tinyioc.aop;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Date;

@Aspect
public class LogTrackAspect {


    //这里需要注意了，这个是将自己自定义注解作为切点的根据，路径一定要写正确了
    @Pointcut(value = "execution(* us.codecraft.tinyioc.*.*(..))")
    public void access() {

    }

    //进来切点世界，先经过的第一个站
    @Before("access()")
    public void before(JoinPoint joinPoint) throws Throwable {
        System.out.println("-aop 日志记录启动-" + new Date());
        /*MethodInvocationProceedingJoinPoint jp = (MethodInvocationProceedingJoinPoint)joinPoint;
        ReflectiveMethodInvocation methodInvocation = (ReflectiveMethodInvocation)jp.getMethodInvocation();
        methodInvocation.invokeJoinpoint();*/
    }





    //进来切点这，最后经过的一个站，也是方法正常运行结束后
    @After("access()")
    public void after(JoinPoint joinPoint) {
        System.out.println("-aop 日志记录结束-" + new Date());
    }
}
