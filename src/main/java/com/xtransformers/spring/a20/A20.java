package com.xtransformers.spring.a20;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class A20 {

    private static final Logger log = LoggerFactory.getLogger(A20.class);

    public static void main(String[] args) throws Exception {
        AnnotationConfigServletWebServerApplicationContext context =
                new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);

        // 解析 @RequestMapping 以及派生注解，生成路径与控制器方法的映射关系，在初始化时就生成
        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);

        // 获取映射结果
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();

        handlerMethods.forEach((k, v) -> {
            System.out.println(k + " = " + v);
        });
        /**
         * {GET [/test1]} = com.xtransformers.spring.a20.Controller1#test1()
         * {POST [/test2]} = com.xtransformers.spring.a20.Controller1#test2(String)
         * {PUT [/test3]} = com.xtransformers.spring.a20.Controller1#test3(String)
         * { [/test4]} = com.xtransformers.spring.a20.Controller1#test4()
         */

        // 模拟请求 返回处理器执行链
        HandlerExecutionChain chain = handlerMapping.getHandler(new MockHttpServletRequest("GET", "/test1"));
        System.out.println("chain = " + chain);
        // chain = HandlerExecutionChain with [com.xtransformers.spring.a20.Controller1#test1()] and 0 interceptors

    }


}
