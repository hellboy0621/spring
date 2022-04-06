package com.xtransformers.spring.a05.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 * @author daniel
 * @date 2022-04-05
 */
@Controller
public class Bean3 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bean3.class);

    public Bean3() {
        LOGGER.info("Constructor. 被 Spring 管理了");
    }
}
