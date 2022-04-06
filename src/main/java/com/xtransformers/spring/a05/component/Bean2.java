package com.xtransformers.spring.a05.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author daniel
 * @date 2022-04-05
 */
@Component
public class Bean2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bean2.class);

    public Bean2() {
        LOGGER.info("Constructor. 被 Spring 管理了");
    }
}
