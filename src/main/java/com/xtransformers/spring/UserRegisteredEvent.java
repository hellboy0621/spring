package com.xtransformers.spring;

import org.springframework.context.ApplicationEvent;

/**
 * @author daniel
 * @date 2022-04-01
 */
public class UserRegisteredEvent extends ApplicationEvent {
    public UserRegisteredEvent(Object source) {
        super(source);
    }
}
