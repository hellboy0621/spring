package com.xtransformers.spring.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.Controller;

/**
 * 常见 ApplicationContext 实现
 *
 * @author daniel
 * @date 2022-04-02
 */
public class ApplicationContextTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContextTest.class);

    public static void main(String[] args) {
        // testClassPathXmlApplicationContext();
        // testFileSystemXmlApplicationContext();

        // testXmlBeanDefinitionReader();

        // testAnnotationConfigApplicationContext();
        // testAnnotationConfigServletWebApplicationContext();
    }

    private static void testXmlBeanDefinitionReader() {
        Resource resource = new ClassPathResource("b01.xml");
        xml2Bean(resource);
        // 这里如果以  / 开头，会认为是绝对路径；
        // 不以 / 开头，相对路径
        resource = new FileSystemResource("src/main/resources/b01.xml");
        resource = new FileSystemResource("/Users/daniel/Documents/code/spring/src/main/resources/b01.xml");
        xml2Bean(resource);
    }

    private static void xml2Bean(Resource resource) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        for (String name : beanFactory.getBeanDefinitionNames()) {
            LOGGER.info("before xml bean definition reader load, name: {}", name);
        }
        XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
        beanDefinitionReader.loadBeanDefinitions(resource);
        for (String name : beanFactory.getBeanDefinitionNames()) {
            LOGGER.info("after xml bean definition reader load, name: {}", name);
        }
    }

    /**
     * 较为经典的容器，基于 classpath 下 xml 格式的配置文件来创建
     */
    private static void testClassPathXmlApplicationContext() {
        ClassPathXmlApplicationContext context
                = new ClassPathXmlApplicationContext("b01.xml");
        checkBean(context);
    }

    private static void checkBean(ApplicationContext context) {
        for (String name : context.getBeanDefinitionNames()) {
            LOGGER.info(name);
        }
        LOGGER.info("bean2's bean1 {}", context.getBean(Bean2.class).getBean1());
    }

    /**
     * 基于磁盘路径下 xml 格式的配置文件来创建
     */
    private static void testFileSystemXmlApplicationContext() {
        // 需要搭配设置 Working directory
        String configLocation = "/src/main/resources/b01.xml";
        FileSystemXmlApplicationContext context =
                new FileSystemXmlApplicationContext(configLocation);
        checkBean(context);
    }

    /**
     * 较为经典的容器，基于 java 配置类来创建
     */
    private static void testAnnotationConfigApplicationContext() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(Config.class);
        // 这里可以看到 BeanFactory 5个后处理器
        /*
        org.springframework.context.annotation.internalConfigurationAnnotationProcessor
        org.springframework.context.annotation.internalAutowiredAnnotationProcessor
        org.springframework.context.annotation.internalCommonAnnotationProcessor
        org.springframework.context.event.internalEventListenerProcessor
        org.springframework.context.event.internalEventListenerFactory
         */
        // 与 xml 方式增加 <context:annotation-config /> 效果一致
        checkBean(context);
    }

    /**
     * 较为经典的容器，基于 java 配置类来创建，用于 web 环境
     */
    private static void testAnnotationConfigServletWebApplicationContext() {
        AnnotationConfigServletWebServerApplicationContext context =
                new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
        // 默认绑定8080端口
        // http://localhost:8080/hello
    }

    @Configuration
    static class WebConfig {
        @Bean
        public ServletWebServerFactory servletWebServerFactory() {
            return new TomcatServletWebServerFactory();
        }

        @Bean
        public DispatcherServlet dispatcherservlet() {
            return new DispatcherServlet();
        }

        @Bean
        public DispatcherServletRegistrationBean dispatcherServletRegistrationBean(
                DispatcherServlet dispatcherServlet) {
            return new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        }

        @Bean("/hello")
        public Controller controller1() {
            return (request, response) -> {
                response.getWriter().write("Hello Controller1.");
                return null;
            };
        }
    }

    @Configuration
    static class Config {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            Bean2 bean2 = new Bean2();
            bean2.setBean1(bean1());
            return bean2;
        }
    }

    static class Bean1 {
    }

    static class Bean2 {
        private Bean1 bean1;

        public void setBean1(Bean1 bean1) {
            this.bean1 = bean1;
        }

        public Bean1 getBean1() {
            return bean1;
        }
    }

}
