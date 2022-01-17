package us.codecraft.tinyioc.aop;

import java.lang.reflect.Method;
import java.util.List;

public interface AdvisorChainFactory {

    List<Object> getInterceptorsAndDynamicInterceptionAdvice(AdvisedSupport config, Method method, Class<?> targetClass);
}
