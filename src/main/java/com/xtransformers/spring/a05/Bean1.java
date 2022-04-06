package com.xtransformers.spring.a05;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author daniel
 * @date 2022-04-05
 */
public class Bean1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bean1.class);

    public Bean1() {
        LOGGER.info("Constructor. 被 Spring 管理了");
    }
}
