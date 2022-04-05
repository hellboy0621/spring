package com.xtransformers.spring.a03;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author daniel
 * @date 2022-04-05
 */
@Component
public class LifeCycleBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(LifeCycleBean.class);

    public LifeCycleBean() {
        LOGGER.info("Constructor");
    }

    @Autowired
    public void injection(@Value("${JAVA_HOME}") String name) {
        LOGGER.info("Dependency injection name:{}", name);
    }

    @PostConstruct
    public void init() {
        LOGGER.info("init @PostConstruct");
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info("destroy @PreDestroy");
    }
}
