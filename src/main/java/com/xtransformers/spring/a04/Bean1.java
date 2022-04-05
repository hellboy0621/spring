package com.xtransformers.spring.a04;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author daniel
 * @date 2022-04-05
 */
public class Bean1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bean1.class);

    private Bean2 bean2;

    @Autowired
    public void setBean2(Bean2 bean2) {
        LOGGER.info("@Autowired 生效 {}", bean2);
        this.bean2 = bean2;
    }

    private Bean3 bean3;

    @Resource
    public void setBean3(Bean3 bean3) {
        LOGGER.info("@Resource 生效 {}", bean3);
        this.bean3 = bean3;
    }

    private String home;

    /**
     * // @Autowired 注解对于 String 类型的参数，不会查找 bean，会注入失败
     * 所以要配合 @Value 注解使用
     *
     * @param home
     */
    @Autowired
    public void setHome(@Value("${JAVA_HOME}") String home) {
        LOGGER.info("@Value 生效 {}", home);
        this.home = home;
    }

    @PostConstruct
    public void init() {
        LOGGER.info("@PostConstruct 生效");
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info("@PreDestroy 生效");
    }

    @Override
    public String toString() {
        return "Bean1{" +
                "bean2=" + bean2 +
                ", bean3=" + bean3 +
                ", home='" + home + '\'' +
                '}';
    }
}
