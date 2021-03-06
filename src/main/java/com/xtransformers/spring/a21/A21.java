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
                @ModelAttribute("user1") User user1,
                // ???????????? mavContainer.getModel() = {user1=User{name='??????', age=18}, org.springframework.validation.BindingResult.user1=org.springframework.validation.BeanPropertyBindingResult: 0 errors}
                User user2,
                // ???????????? mavContainer.getModel() = {user1=User{name='??????', age=18}, org.springframework.validation.BindingResult.user1=org.springframework.validation.BeanPropertyBindingResult: 0 errors, user=User{name='??????', age=18}, org.springframework.validation.BindingResult.user=org.springframework.validation.BeanPropertyBindingResult: 0 errors}
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

        // 1. ??????????????????????????? HandlerMethod ?????????HandlerMapping???
        HandlerMethod handlerMethod = new HandlerMethod(new Controller(),
                Controller.class.getMethod("test", String.class, String.class, int.class, String.class,
                        MultipartFile.class, int.class, String.class, String.class, String.class,
                        HttpServletRequest.class, User.class, User.class, User.class));

        // 2. ?????????????????????????????????
        // @RequestParam("age") int age ??? String ???????????????????????? Integer
        ServletRequestDataBinderFactory binderFactory = new ServletRequestDataBinderFactory(null, null);

        // 3. ?????? ModelAndViewContainer ?????????????????? Model ??????
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();

        // 4. ?????????????????????
        // beanFactory ?????? ${} ????????????
        // true ?????????????????? @RequestParam ??????
        // false ??????????????? @RequestParam ??????
        RequestParamMethodArgumentResolver requestParamResolver =
                new RequestParamMethodArgumentResolver(beanFactory, false);

        // ??????
        HandlerMethodArgumentResolverComposite composite = new HandlerMethodArgumentResolverComposite();
        composite.addResolvers(
                requestParamResolver,
                new PathVariableMethodArgumentResolver(),
                new RequestHeaderMethodArgumentResolver(beanFactory),
                new ServletCookieValueMethodArgumentResolver(beanFactory),
                new ExpressionValueMethodArgumentResolver(beanFactory),
                new ServletRequestMethodArgumentResolver(),
                // false ??????????????? @ModelAttribute ??????
                new ServletModelAttributeMethodProcessor(false),
                // RequestResponseBodyMethodProcessor ??? ServletModelAttributeMethodProcessor(true) ??????????????????????????????????????????????????????
                new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())),
                // true ?????????????????? @ModelAttribute ?????? ?????????????????????????????? json ?????????????????????????????????
                new ServletModelAttributeMethodProcessor(true)
        );

        for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
            // ?????????????????????????????????????????????????????????methodParameter.getParameterName() ??? null
            methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());

            // [0] @RequestParam String name1
            String annotationStr = Arrays.stream(methodParameter.getParameterAnnotations())
                    .map(each -> each.annotationType().getSimpleName()).collect(
                            Collectors.joining());
            annotationStr = annotationStr.length() == 0 ? "" : " @" + annotationStr;

            if (composite.supportsParameter(methodParameter)) {
                // ???????????????
                Object value = composite.resolveArgument(methodParameter, mavContainer,
                        new ServletWebRequest(request), binderFactory);
                // System.out.println("value -> " + value.getClass());
                final String formatter = "[%s]%s %s %s -> %s\n";
                System.out.printf(formatter, methodParameter.getParameterIndex(), annotationStr,
                        methodParameter.getParameterType().getSimpleName(), methodParameter.getParameterName(), value);
                System.out.println("???????????? mavContainer.getModel() = " + mavContainer.getModel());
            } else {
                // ?????????
                final String formatter = "[%s]%s %s %s\n";
                System.out.printf(formatter, methodParameter.getParameterIndex(), annotationStr,
                        methodParameter.getParameterType().getSimpleName(), methodParameter.getParameterName());
            }
        }

        /**
         * a. ??????????????????????????????
         *  1. ?????????????????????????????? supportsParameter
         *  2. ??????????????? resolveArgument
         * b. ????????????
         * c. @RequestParam @CookieValue ??????????????????????????????????????????????????? ${} #{} ?????????
         */

    }

    private static HttpServletRequest mockRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("name1", "??????");
        request.setParameter("name2", "??????");
        request.addPart(new MockPart("file", "abc", "hello".getBytes(StandardCharsets.UTF_8)));
        Map<String, String> uriTemplateVariables =
                new AntPathMatcher().extractUriTemplateVariables("/test/{id}", "/test/123");
        // System.out.println("uriTemplateVariables = " + uriTemplateVariables);
        // ???????????? HandlerMapping ???????????????????????????????????????????????????????????????????????????????????? request
        // uriTemplateVariables = {id=123}
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVariables);
        request.setContentType("application/json");
        request.setCookies(new Cookie("token", "123456"));
        request.setParameter("name", "??????");
        request.setParameter("age", "18");
        request.setContent("""
                     {
                         "name": "??????",
                         "age": 20
                     }
                """.getBytes(StandardCharsets.UTF_8));
        return new StandardServletMultipartResolver().resolveMultipart(request);
    }
}
