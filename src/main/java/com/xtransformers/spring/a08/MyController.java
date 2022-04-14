package com.xtransformers.spring.a08;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author daniel
 * @date 2022-04-13
 */
@RestController
public class MyController {

    @Lazy
    @Resource
    private BeanForSingleton beanForSingleton;

    @Lazy
    @Resource
    private BeanForPrototype beanForPrototype;

    @Lazy
    @Resource
    private BeanForRequest beanForRequest;

    @Lazy
    @Resource
    private BeanForSession beanForSession;

    @Lazy
    @Resource
    private BeanForApplication beanForApplication;

    @GetMapping(value = "/test", produces = "text/html")
    public String test(HttpServletRequest request, HttpSession session) {
        ServletContext sc = request.getServletContext();
        String result = "<ul>" +
                "<li> single scope : " + beanForSingleton + "</li>" +
                "<li> prototype scope : " + beanForPrototype + "</li>" +
                "<li> request scope : " + beanForRequest + "</li>" +
                "<li> session scope : " + beanForSession + "</li>" +
                "<li> application scope : " + beanForApplication + "</li>" +
                "</ul>";
        return result;
    }
}
