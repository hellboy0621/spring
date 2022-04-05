package com.xtransformers.spring.a04;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.StandardEnvironment;

/**
 * @author daniel
 * @date 2022-04-05
 */
public class DigInAutowired {

    public static void main(String[] args)
            throws Throwable {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // beanFactory.registerSingleton 认为单例对象已经创建好了，不会再执行 创建过程、依赖注入、初始化 阶段了
        beanFactory.registerSingleton("bean2", new Bean2());
        beanFactory.registerSingleton("bean3", new Bean3());
        // 正确解析 @Value
        beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        // ${} 的解析器
        beanFactory.addEmbeddedValueResolver(new StandardEnvironment()::resolvePlaceholders);

        AutowiredAnnotationBeanPostProcessor processor = new AutowiredAnnotationBeanPostProcessor();
        processor.setBeanFactory(beanFactory);

        Bean1 bean1 = new Bean1();
        // Bean1{bean2=null, bean3=null, home='null'}
        System.out.println(bean1);

        // processor.postProcessProperties(null, bean1, "bean1");
        // Bean1{bean2=com.xtransformers.spring.a04.Bean2@4fb64261, bean3=null, home='/Users/daniel/.jenv/versions/1
        // .8.0.301'}
        // System.out.println(bean1);

        // Method findAutowiringMetadata =
        //         AutowiredAnnotationBeanPostProcessor.class.getDeclaredMethod("findAutowiringMetadata", String.class,
        //                 Class.class, PropertyValues.class);
        // findAutowiringMetadata.setAccessible(true);
        // 获取 Bean1 加了 @Autowired @Value 的成员变量、方法参数信息
        // InjectionMetadata metadata =
        //         (InjectionMetadata) findAutowiringMetadata.invoke(processor, "bean1", Bean1.class, null);

        // 调用 InjectionMetadata 来进行依赖注入，注入时按类型查找值
        // metadata.inject(bean1, "bean1", null);

        // Bean1{bean2=com.xtransformers.spring.a04.Bean2@4fb64261, bean3=null, home='/Users/daniel/.jenv/versions/1
        // .8.0.301'}
        // System.out.println(bean1);
        // Bean1{bean2=com.xtransformers.spring.a04.Bean2@150c158, bean3=com.xtransformers.spring.a04.Bean3@fe18270,
        // home='/Users/daniel/.jenv/versions/1.8.0.301'}

        // 按照类型查找值
        // 获取成员变量 这里给 Bean1 中的 bean3 增加 @Autowired 注解
        Field bean3 = Bean1.class.getDeclaredField("bean3");
        DependencyDescriptor dd1 = new DependencyDescriptor(bean3, false);
        Object o1 = beanFactory.doResolveDependency(dd1, null, null, null);
        // com.xtransformers.spring.a04.Bean3@fe18270
        System.out.println(o1);

        // 获取
        Method setBean2 = Bean1.class.getDeclaredMethod("setBean2", Bean2.class);
        DependencyDescriptor dd2 = new DependencyDescriptor(new MethodParameter(setBean2, 0), false);
        Object o2 = beanFactory.doResolveDependency(dd2, null, null, null);
        System.out.println(o2);

        Method setHome = Bean1.class.getDeclaredMethod("setHome", String.class);
        DependencyDescriptor dd3 = new DependencyDescriptor(new MethodParameter(setHome, 0), false);
        Object o3 = beanFactory.doResolveDependency(dd3, null, null, null);
        System.out.println(o3);
    }
}
