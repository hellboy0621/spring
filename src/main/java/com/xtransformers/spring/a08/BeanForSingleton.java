package com.xtransformers.spring.a08;

import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author daniel
 * @date 2022-04-13
 */
@Scope("singleton")
@Component
public class BeanForSingleton {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanForSingleton.class);

    @PreDestroy
    public void destroy() {
        LOGGER.info("destroy");
    }
}
