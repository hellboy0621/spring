package com.xtransformers.spring.a06;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;

/**
 * @author daniel
 * @date 2022-04-10
 */
public class MyBean implements BeanNameAware, BeanFactoryAware, ApplicationContextAware, EmbeddedValueResolverAware,
        InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyBean.class);

    @Override
    public void setBeanName(String name) {
        LOGGER.info("this obj: {}, bean name: {}", this, name);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        LOGGER.info("this obj: {}, beanFactory: {}", this, beanFactory);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOGGER.info("this obj: {}, applicationContext: {}", this, applicationContext);
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        String javaVersion = resolver.resolveStringValue("${java.version}");
        LOGGER.info("javaVersion: {}", javaVersion);
        LOGGER.info("this obj: {}, StringValueResolver: {}", this, resolver);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("this obj: {} init", this);
    }

    // 需要 AutowiredAnnotationBeanPostProcessor 才能执行
    @Autowired
    public void aaa(ApplicationContext applicationContext) {
        LOGGER.info("use @Autowired this obj: {}, applicationContext: {}", this, applicationContext);
    }

    // 需要 CommonAnnotationBeanPostProcessor 才能执行
    @PostConstruct
    public void init() {
        LOGGER.info("use @PostConstruct this obj: {} init", this);
    }
}
