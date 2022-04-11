package com.xtransformers.spring.a07;

import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

/**
 * @author daniel
 * @date 2022-04-11
 */
public class Bean2 implements DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bean2.class);

    @PreDestroy
    public void destroy1() {
        LOGGER.info("destroy1 @PreDestroy");
    }


    @Override
    public void destroy() throws Exception {
        LOGGER.info("destroy2 DisposableBean#destroy");
    }

    public void destroy3() {
        LOGGER.info("destroy3 @Bean destroyMethod");
    }
}
