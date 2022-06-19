package com.xtransformers.spring.a21;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletCookieValueMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletModelAttributeMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestMethodArgumentResolver;

public class A21 {

    static class Controller {
        public void test(
                @RequestParam("name1") String name1,
                String name2,
                @RequestParam("age") int age,
                @RequestParam(name = "home", defaultValue = "${GOPATH}") String home1,
                @RequestParam("file") MultipartFile file,
                @PathVariable("id") int id,
                @RequestHeader("Content-Type") String header,
                @CookieValue("token") String token,
                @Value("${GOPATH}") String home2,
                HttpServletRequest request,
                @ModelAttribute User user1,
                User user2,
                @RequestBody User user3
        ) {
        }
    }

    static class User {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
        HttpServletRequest request = mockRequest();

        // 1. 控制器方法被封装为 HandlerMethod （脱离HandlerMapping）
        HandlerMethod handlerMethod = new HandlerMethod(new Controller(),
                Controller.class.getMethod("test", String.class, String.class, int.class, String.class,
                        MultipartFile.class, int.class, String.class, String.class, String.class,
                        HttpServletRequest.class, User.class, User.class, User.class));

        // 2. 准备对象绑定与类型转换
        // @RequestParam("age") int age 将 String 类型的数据转换为 Integer
        ServletRequestDataBinderFactory binderFactory = new ServletRequestDataBinderFactory(null, null);

        // 3. 准备 ModelAndViewContainer 用来存储中间 Model 结果
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();

        // 4. 解析每个参数值
        // beanFactory 解析 ${} 环境变量
        // true 表示可以不加 @RequestParam 注解
        // false 表示必须加 @RequestParam 注解
        RequestParamMethodArgumentResolver requestParamResolver =
                new RequestParamMethodArgumentResolver(beanFactory, false);

        // 组合
        HandlerMethodArgumentResolverComposite composite = new HandlerMethodArgumentResolverComposite();
        composite.addResolvers(
                requestParamResolver,
                new PathVariableMethodArgumentResolver(),
                new RequestHeaderMethodArgumentResolver(beanFactory),
                new ServletCookieValueMethodArgumentResolver(beanFactory),
                new ExpressionValueMethodArgumentResolver(beanFactory),
                new ServletRequestMethodArgumentResolver(),
                // false 表示必须有 @ModelAttribute 注解
                new ServletModelAttributeMethodProcessor(false),
                // RequestResponseBodyMethodProcessor 与 ServletModelAttributeMethodProcessor(true) 顺序很重要，不能错，否则解析数据错误
                new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())),
                // true 表示可以没有 @ModelAttribute 注解 前面如果没有正确解析 json 的处理器，就会错误赋值
                new ServletModelAttributeMethodProcessor(true)
        );

        for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
            // 参数名称解析器，不增加无法解析参数名，methodParameter.getParameterName() 为 null
            methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());

            // [0] @RequestParam String name1
            String annotationStr = Arrays.stream(methodParameter.getParameterAnnotations())
                    .map(each -> each.annotationType().getSimpleName()).collect(
                            Collectors.joining());
            annotationStr = annotationStr.length() == 0 ? "" : " @" + annotationStr;

            if (composite.supportsParameter(methodParameter)) {
                // 支持，解析
                Object value = composite.resolveArgument(methodParameter, mavContainer,
                        new ServletWebRequest(request), binderFactory);
                // System.out.println("value -> " + value.getClass());
                final String formatter = "[%s]%s %s %s -> %s\n";
                System.out.printf(formatter, methodParameter.getParameterIndex(), annotationStr,
                        methodParameter.getParameterType().getSimpleName(), methodParameter.getParameterName(), value);
            } else {
                // 不支持
                final String formatter = "[%s]%s %s %s\n";
                System.out.printf(formatter, methodParameter.getParameterIndex(), annotationStr,
                        methodParameter.getParameterType().getSimpleName(), methodParameter.getParameterName());
            }
        }

    }

    private static HttpServletRequest mockRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("name1", "张三");
        request.setParameter("name2", "李四");
        request.addPart(new MockPart("file", "abc", "hello".getBytes(StandardCharsets.UTF_8)));
        Map<String, String> uriTemplateVariables =
                new AntPathMatcher().extractUriTemplateVariables("/test/{id}", "/test/123");
        // System.out.println("uriTemplateVariables = " + uriTemplateVariables);
        // 本应该是 HandlerMapping 做的映射工作，因为没有引入，所以通过工具类把对应关系给到 request
        // uriTemplateVariables = {id=123}
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVariables);
        request.setContentType("application/json");
        request.setCookies(new Cookie("token", "123456"));
        request.setParameter("name", "王五");
        request.setParameter("age", "18");
        request.setContent("""
                     {
                         "name": "赵六",
                         "age": 20
                     }
                """.getBytes(StandardCharsets.UTF_8));
        return new StandardServletMultipartResolver().resolveMultipart(request);
    }
}
