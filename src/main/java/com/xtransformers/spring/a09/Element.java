package com.xtransformers.spring.a09;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

    @Autowired
    private ObjectFactory<BeanForPrototype3> beanForPrototype3;
    
    @Autowired
    private ApplicationContext applicationContext;

    public BeanForPrototype getBeanForPrototype() {
        return beanForPrototype;
    }

    public BeanForPrototype2 getBeanForPrototype2() {
        return beanForPrototype2;
    }

    public BeanForPrototype3 getBeanForPrototype3() {
        return beanForPrototype3.getObject();
    }

    public BeanForPrototype4 getBeanForPrototype4() {
        return applicationContext.getBean(BeanForPrototype4.class);
    }
    
}
