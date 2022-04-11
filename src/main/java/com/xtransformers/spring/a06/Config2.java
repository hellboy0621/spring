package com.xtransformers.spring.a06;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author daniel
 * @date 2022-04-11
 */
@Configuration
public class Config2 implements InitializingBean, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config2.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("init");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOGGER.info("injection application context");
    }

    @Bean
    public BeanFactoryPostProcessor processor2() {
        return beanFactory -> {
            LOGGER.info("process2");
        };
    }
}
