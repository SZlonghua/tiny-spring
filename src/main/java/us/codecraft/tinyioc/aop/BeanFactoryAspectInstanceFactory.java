package us.codecraft.tinyioc.aop;

import us.codecraft.tinyioc.beans.factory.BeanFactory;

import java.io.Serializable;


public class BeanFactoryAspectInstanceFactory implements AspectInstanceFactory, Serializable {

    private final BeanFactory beanFactory;

    private final String name;

    private final Class<?> resolvedType;

    public BeanFactoryAspectInstanceFactory(BeanFactory beanFactory, String name) {
        this.beanFactory = beanFactory;
        this.name = name;
        this.resolvedType = beanFactory.getType(name);
    }


    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public String getName() {
        return name;
    }

    public Class<?> getResolvedType() {
        return resolvedType;
    }

    @Override
    public Object getAspectInstance() {
        try {
            return this.beanFactory.getBean(this.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
