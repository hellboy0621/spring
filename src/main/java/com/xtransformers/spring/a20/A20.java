package com.xtransformers.spring.a20;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
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
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test1");
        HandlerExecutionChain chain = handlerMapping.getHandler(request);
        System.out.println("chain = " + chain);
        // chain = HandlerExecutionChain with [com.xtransformers.spring.a20.Controller1#test1()] and 0 interceptors
        if (chain == null) {
            return;
        }

        // 调用控制器方法
        CustomRequestMappingHandlerAdapter handlerAdapter = context.getBean(CustomRequestMappingHandlerAdapter.class);
        MockHttpServletResponse response = new MockHttpServletResponse();
        handlerAdapter.invokeHandlerMethod(request, response, (HandlerMethod) chain.getHandler());
        // [DEBUG] 10:41:09.344 [main] c.x.spring.a20.Controller1          - test1()

        request = new MockHttpServletRequest("POST", "/test2");
        request.setParameter("name", "Adix");
        chain = handlerMapping.getHandler(request);
        System.out.println("chain = " + chain);
        // chain = HandlerExecutionChain with [com.xtransformers.spring.a20.Controller1#test2(String)] and 0
        // interceptors
        if (chain == null) {
            return;
        }
        handlerAdapter.invokeHandlerMethod(request, response, (HandlerMethod) chain.getHandler());
        // [DEBUG] 10:41:09.355 [main] c.x.spring.a20.Controller1          - test2(Adix)

        System.out.println("所有参数解析器");
        // 把自定义的 TokenArgumentResolver 放在责任链的最前面
        List<HandlerMethodArgumentResolver> newArgumentResolvers =
                new ArrayList<>(handlerAdapter.getArgumentResolvers());
        newArgumentResolvers.add(0, new TokenArgumentResolver());
        handlerAdapter.setArgumentResolvers(newArgumentResolvers);
        for (HandlerMethodArgumentResolver each : handlerAdapter.getArgumentResolvers()) {
            System.out.println(each);
        }

        System.out.println("所有返回值解析器");
        ArrayList<HandlerMethodReturnValueHandler> newReturnValueHandlers =
                new ArrayList<>(handlerAdapter.getReturnValueHandlers());
        newReturnValueHandlers.add(0, new YmlReturnValueHandler());
        handlerAdapter.setReturnValueHandlers(newReturnValueHandlers);
        for (HandlerMethodReturnValueHandler each : handlerAdapter.getReturnValueHandlers()) {
            System.out.println(each);
        }

        request = new MockHttpServletRequest("PUT", "/test3");
        request.addHeader("token", "TOKEN_123");
        chain = handlerMapping.getHandler(request);
        System.out.println("chain = " + chain);

        if (chain == null) {
            return;
        }
        handlerAdapter.invokeHandlerMethod(request, response, (HandlerMethod) chain.getHandler());

        request = new MockHttpServletRequest("GET", "/test4");
        chain = handlerMapping.getHandler(request);
        System.out.println("chain = " + chain);
        // chain = HandlerExecutionChain with [com.xtransformers.spring.a20.Controller1#test4()] and 0 interceptors
        if (chain == null) {
            return;
        }
        handlerAdapter.invokeHandlerMethod(request, response, (HandlerMethod) chain.getHandler());
        // 查看响应结果
        String test4Result = response.getContentAsString();
        System.out.println("test4Result = " + test4Result);
        // test4Result = !!com.xtransformers.spring.a20.Controller1$User {age: 18, name: 张三}
    }


}
