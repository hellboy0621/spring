package com.xtransformers.spring.a03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author daniel
 * @date 2022-04-05
 */
@Component
public class BeanPostProcessorTest implements InstantiationAwareBeanPostProcessor, DestructionAwareBeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanPostProcessorTest.class);

    String lifeCycleBean = "lifeCycleBean";

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        if (lifeCycleBean.equals(beanName)) {
            LOGGER.info("postProcessBeforeInstantiation <<< 实例化之前执行，这里返回的对象如果不为 null 就会替换掉原本的 bean");
        }
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if (lifeCycleBean.equals(beanName)) {
            LOGGER.info("postProcessAfterInstantiation <<< 实例化之后执行，这里返回 false 会跳过依赖注入阶段");
            // return false;
        }
        return true;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
            throws BeansException {
        if (lifeCycleBean.equals(beanName)) {
            LOGGER.info("postProcessProperties 依赖注入阶段执行，如 @Autowired @Value @Resource");
        }
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (lifeCycleBean.equals(beanName)) {
            LOGGER.info("postProcessBeforeInitialization <<< 初始化之前执行，这里返回的对象不为 null 会替换掉原本的 bean，如 @PostConstruct @ConfigurationProperties");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (lifeCycleBean.equals(beanName)) {
            LOGGER.info("postProcessAfterInitialization <<< 初始化之后执行，这里返回的对象会替换掉原本的 bean，如 代理增强");
        }
        return bean;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (lifeCycleBean.equals(beanName)) {
            LOGGER.info("postProcessBeforeDestruction <<< 销毁之前执行，如 @PreDestroy");
        }
    }
}
