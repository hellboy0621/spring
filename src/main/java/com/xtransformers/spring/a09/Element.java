package com.xtransformers.spring.a09;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @author daniel
 * @date 2022-04-14
 */
@Component
public class Element {

    @Lazy
    @Autowired
    private BeanForPrototype beanForPrototype;

    @Autowired
    private BeanForPrototype2 beanForPrototype2;

    public BeanForPrototype getBeanForPrototype() {
        return beanForPrototype;
    }

    public BeanForPrototype2 getBeanForPrototype2() {
        return beanForPrototype2;
    }
}
