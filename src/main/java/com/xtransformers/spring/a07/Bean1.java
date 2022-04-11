package com.xtransformers.spring.a07;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author daniel
 * @date 2022-04-11
 */
public class Bean1 implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bean1.class);

    /**
     * 通过 Bean 后处理解析 @PostConstruct 来处理的
     */
    @PostConstruct
    public void init1() {
        LOGGER.info("init1 @PostConstruct");
    }

    /**
     * Aware 接口，初始化顺序位于 init1() 和 afterPropertiesSet() 之间
     */

    /**
     * 实现 InitializingBean 接口
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("init2 InitializingBean#afterPropertiesSet");
    }

    /**
     * @Bean(initMethod = "init3")
     * 加在了 BeanDefinition 中
     */
    public void init3() {
        LOGGER.info("init3 @Bean initMethod");
    }
}
