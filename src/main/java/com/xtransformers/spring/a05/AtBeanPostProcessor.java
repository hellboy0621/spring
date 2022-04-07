package com.xtransformers.spring.a05;

import java.io.IOException;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

/**
 * 自定义注解 模拟 @Bean 功能
 *
 * @author daniel
 * @date 2022-04-07
 */
public class AtBeanPostProcessor implements BeanFactoryPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtBeanPostProcessor.class);

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory)
            throws BeansException {
        try {
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

                if (configurableListableBeanFactory instanceof DefaultListableBeanFactory beanFactory) {
                    // 方法名作为 Bean 的名字
                    beanFactory.registerBeanDefinition(methodMetadata.getMethodName(), beanDefinition);
                }
            }
        } catch (IOException e) {
            LOGGER.error("@Bean postProcessBeanFactory error ", e);
        }
    }
}
