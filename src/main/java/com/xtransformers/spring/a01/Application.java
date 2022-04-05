package com.xtransformers.spring.a01;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;

/**
 * @author daniel
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, IOException {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        // 获取单例Bean容器
        Field singletonObjects = DefaultSingletonBeanRegistry.class.getDeclaredField("singletonObjects");
        singletonObjects.setAccessible(true);
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) singletonObjects.get(beanFactory);
        map.entrySet().stream()
                .filter(each -> each.getKey().startsWith("component"))
                .forEach(each -> System.out.println(each.getKey() + " = " + each.getValue()));

        // 1. 国际化
        System.out.println(context.getMessage("head.title", null, Locale.CHINA));
        System.out.println(context.getMessage("head.title", null, Locale.ENGLISH));

        // 2. 根据通配符获取一组资源
        Resource[] resources = context.getResources("classpath:application.properties");
        for (Resource resource : resources) {
            System.out.println(resource);
        }
        resources = context.getResources("classpath*:META-INF/spring.factories");
        for (Resource resource : resources) {
            System.out.println(resource);
        }

        // 3. 获取环境变量或配置文件
        System.out.println(context.getEnvironment().getProperty("java_home"));
        System.out.println(context.getEnvironment().getProperty("server.port"));

        // 4. 发送事件
        context.publishEvent(new UserRegisteredEvent(context));

    }

}
