package com.xtransformers.spring;

import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author daniel
 * @date 2022-03-31
 */
@Component
public class Component2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(Component2.class);

    /**
     * 事件监听器
     *
     * @param event 监听事件对象
     */
    @EventListener
    public void listen(UserRegisteredEvent event) {
        LOGGER.info("received event {}", JSONUtil.toJsonStr(event));
    }

}
