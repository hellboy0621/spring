package com.xtransformers.spring.a04;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author daniel
 * @date 2022-04-05
 */
public class A04Application {

    public static void main(String[] args) {
        // GenericApplicationContext 是一个【干净】的容器，排除其他干扰
        GenericApplicationContext context = new GenericApplicationContext();

        // 用原始方法注册三个 bean
        context.registerBean("bean1", Bean1.class);
        context.registerBean("bean2", Bean2.class);
        context.registerBean("bean3", Bean3.class);
        context.registerBean("bean4", Bean4.class);

        // 解决 @Value 注入为 String 的变量
        //         Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of
        //         type 'java.lang.String' available: expected at least 1 bean which qualifies as autowire candidate.
        //         Dependency annotations: {@org.springframework.beans.factory.annotation.Value("${JAVA_HOME}")}
        context.getDefaultListableBeanFactory()
                .setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        // 解析 @Autowired @Value
        context.registerBean(AutowiredAnnotationBeanPostProcessor.class);

        // 解析 @Resource @PostConstruct @PreDestroy
        context.registerBean(CommonAnnotationBeanPostProcessor.class);

        // 解析 @ConfigurationProperties
        ConfigurationPropertiesBindingPostProcessor.register(context.getDefaultListableBeanFactory());

        // 初始化容器
        context.refresh();

        // 打印 Bean4 查看是否正确注入属性
        // Bean4{home='/Library/Java/JavaVirtualMachines/jdk-17.0.1.jdk/Contents/Home', version='17.0.1'}
        System.out.println(context.getBean(Bean4.class));

        // 销毁容器
        context.close();
    }
}
