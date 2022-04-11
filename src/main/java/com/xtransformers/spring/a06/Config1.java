package com.xtransformers.spring.a06;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author daniel
 * @date 2022-04-11
 */
@Configuration
public class Config1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config1.class);

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        LOGGER.info("context {}", context);
    }

    @PostConstruct
    public void init() {
        LOGGER.info("init");
    }

    // BeanFactory 后处理器
    // 添加了这个后处理器之后导致 上面两个 @Autowired @PostConstruct 方法没有被执行，导致注解失效
    @Bean
    public BeanFactoryPostProcessor processor1() {
        return beanFactory -> {
            LOGGER.info("processor1");
        };
    }
}
