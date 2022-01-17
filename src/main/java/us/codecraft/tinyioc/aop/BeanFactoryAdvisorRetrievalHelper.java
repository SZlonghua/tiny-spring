package us.codecraft.tinyioc.aop;

import us.codecraft.tinyioc.beans.factory.AbstractBeanFactory;
import us.codecraft.tinyioc.beans.factory.BeanFactory;

import java.util.ArrayList;
import java.util.List;

public class BeanFactoryAdvisorRetrievalHelper {

    private final AbstractBeanFactory beanFactory;

    public BeanFactoryAdvisorRetrievalHelper(AbstractBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public List<PointcutAdvisor> findAdvisorBeans() {
        try {
            return beanFactory.getBeansForType(PointcutAdvisor.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<PointcutAdvisor>();
    }
}
