package com.xtransformers.spring.a05;

import java.io.IOException;
import java.util.Set;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;

/**
 * @author daniel
 * @date 2022-04-05
 */
public class A05Application {

    public static void main(String[] args) throws IOException {
        // GenericApplicationContext 是一个【干净】的容器
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("config", Config.class);

        // springNative(context);

        // manual(context);

        // 使用自定义BeanFactory后处理器
        // context.registerBean(ComponentScanPostProcessor.class);

        // 读取 Config 类的元数据信息
        CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
        MetadataReader metadataReader =
                factory.getMetadataReader(new ClassPathResource("com/xtransformers/spring/a05/Config.class"));
        // 获取所有被 @Bean 注解的方法列表
        Set<MethodMetadata> annotatedMethods =
                metadataReader.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());
        for (MethodMetadata methodMetadata : annotatedMethods) {
            System.out.println(methodMetadata);

            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
            // 把 Config 类当成工厂类 @Bean注解的方法为工厂方法
            builder.setFactoryMethodOnBean(methodMetadata.getMethodName(), "config");
            // sqlSessionFactoryBean 需要自动装配，默认关闭的
            builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
            // 设置初始化方法 增加后才会打印如下日志
            // 23:22:43.396 [main] INFO com.alibaba.druid.pool.DruidDataSource - {dataSource-1} inited
            String initMethod =
                    methodMetadata.getAnnotationAttributes(Bean.class.getName()).get("initMethod").toString();
            if (initMethod.length() > 0) {
                builder.setInitMethodName(initMethod);
            }
            AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
            // 方法名作为 Bean 的名字
            context.getDefaultListableBeanFactory()
                    .registerBeanDefinition(methodMetadata.getMethodName(), beanDefinition);
        }

        // 初始化容器
        context.refresh();

        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        // 销毁容器
        context.close();
    }

    // 手动实现
    private static void manual(GenericApplicationContext context) throws IOException {
        // 获取 @ComponentScan 注解中需要扫描的包
        ComponentScan componentScan = AnnotationUtils.findAnnotation(Config.class, ComponentScan.class);
        if (componentScan != null) {
            for (String basePackage : componentScan.basePackages()) {
                System.out.println(basePackage);
                // com.xtransformers.spring.a05.component ->
                // classpath*:com/xtransformers/spring/a05/component/**/*.class
                // 解析包名为文件路径
                String path = "classpath*:" + basePackage.replace(".", "/") + "/**/*.class";
                System.out.println(path);
                CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
                BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
                // 根据文件路径获取资源文件
                for (Resource resource : context.getResources(path)) {
                    System.out.println(resource);
                    MetadataReader metadataReader = factory.getMetadataReader(resource);
                    // 类名
                    String className = metadataReader.getClassMetadata().getClassName();
                    // 是否有 @Component 注解
                    boolean hasAnnotation =
                            metadataReader.getAnnotationMetadata().hasAnnotation(Component.class.getName());
                    // 是否有 @Component 派生注解
                    boolean hasMetaAnnotation =
                            metadataReader.getAnnotationMetadata().hasMetaAnnotation(Component.class.getName());
                    System.out.println(className + " has annotation @Component: " + hasAnnotation
                            + ", has meta annotation @Component " + hasMetaAnnotation);
                    if (hasAnnotation || hasMetaAnnotation) {
                        // 生成 Bean 定义
                        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                                .genericBeanDefinition(className)
                                .getBeanDefinition();
                        // 生成 Bean 名称
                        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
                        String beanName = beanNameGenerator.generateBeanName(beanDefinition,
                                beanFactory);
                        // 注册到 Bean 工厂
                        beanFactory.registerBeanDefinition(beanName, beanDefinition);
                    }
                }
            }
        }
    }

    // Spring 原生实现
    private static void springNative(GenericApplicationContext context) {
        // 增加 BeanFactory 后处理器
        // 解析 @ComponentScan @Bean @Import @ImportResource
        context.registerBean(ConfigurationClassPostProcessor.class);
        // 解析 @MapperScan
        context.registerBean(MapperScannerConfigurer.class, beanDefinition ->
                beanDefinition.getPropertyValues().add("basePackage", "com.xtransformers.spring.a05.mapper"));
    }
}
