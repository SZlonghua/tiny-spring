package us.codecraft.tinyioc.aop;

import org.aspectj.lang.reflect.PerClauseKind;
import us.codecraft.tinyioc.beans.factory.AbstractBeanFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactoryAspectJAdvisorsBuilder {

    private final AbstractBeanFactory beanFactory;

    private final AspectJAdvisorFactory advisorFactory;

    private volatile List<String> aspectBeanNames;

    private final Map<String, List<PointcutAdvisor>> advisorsCache = new ConcurrentHashMap();

    public BeanFactoryAspectJAdvisorsBuilder(AbstractBeanFactory beanFactory, AspectJAdvisorFactory advisorFactory) {
        this.beanFactory = beanFactory;
        this.advisorFactory = advisorFactory;
    }


    public List<PointcutAdvisor> buildAspectJAdvisors() {
        List<String> aspectNames = this.aspectBeanNames;

        if (aspectNames == null) {
            synchronized (this) {
                aspectNames = this.aspectBeanNames;
                if (aspectNames == null) {
                    List<PointcutAdvisor> advisors = new ArrayList<PointcutAdvisor>();
                    aspectNames = new ArrayList<String>();
                    String[] beanNames = beanFactory.getBeanNamesForType(Object.class);
                    for (String beanName : beanNames) {
                        // We must be careful not to instantiate beans eagerly as in this case they
                        // would be cached by the Spring container but would not have been weaved.
                        Class<?> beanType = this.beanFactory.getType(beanName);
                        if (beanType == null) {
                            continue;
                        }
                        if (this.advisorFactory.isAspect(beanType)) {
                            aspectNames.add(beanName);

                            BeanFactoryAspectInstanceFactory factory =
                                    new BeanFactoryAspectInstanceFactory(this.beanFactory, beanName);
                            List<PointcutAdvisor> classAdvisors = this.advisorFactory.getAdvisors(factory);

                            this.advisorsCache.put(beanName, classAdvisors);
                            advisors.addAll(classAdvisors);
                        }
                    }
                    this.aspectBeanNames = aspectNames;
                    return advisors;
                }
            }
        }

        if (aspectNames.isEmpty()) {
            return Collections.emptyList();
        }
        List<PointcutAdvisor> advisors = new ArrayList<PointcutAdvisor>();
        for (String aspectName : aspectNames) {
            List<PointcutAdvisor> cachedAdvisors = this.advisorsCache.get(aspectName);
            if (cachedAdvisors != null) {
                advisors.addAll(cachedAdvisors);
            }
        }
        return advisors;
    }
}
