package com.xtransformers.spring.a05.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author daniel
 * @date 2022-04-05
 */
public class Bean4 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bean4.class);

    public Bean4() {
        LOGGER.info("Constructor. 被 Spring 管理了");
    }
}
