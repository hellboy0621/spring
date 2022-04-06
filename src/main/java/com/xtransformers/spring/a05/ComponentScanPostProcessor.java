package com.xtransformers.spring.a05;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;

/**
 * 自定义包扫描BeanFactory后处理器
 *
 * @author daniel
 * @date 2022-04-06
 */
public class ComponentScanPostProcessor implements BeanFactoryPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentScanPostProcessor.class);

    // context.refresh() 方法执行时会调用此方法
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory)
            throws BeansException {
        try {
            // 获取 @ComponentScan 注解中需要扫描的包
            ComponentScan componentScan = AnnotationUtils.findAnnotation(Config.class, ComponentScan.class);
            if (componentScan != null) {
                for (String basePackage : componentScan.basePackages()) {
                    // com.xtransformers.spring.a05.component ->
                    // classpath*:com/xtransformers/spring/a05/component/**/*.class
                    // 解析包名为文件路径
                    String path = "classpath*:" + basePackage.replace(".", "/") + "/**/*.class";
                    CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
                    BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
                    // 根据文件路径获取资源文件
                    for (Resource resource : new PathMatchingResourcePatternResolver().getResources(path)) {
                        MetadataReader metadataReader = factory.getMetadataReader(resource);
                        // 类名
                        String className = metadataReader.getClassMetadata().getClassName();
                        // 是否有 @Component 注解
                        boolean hasAnnotation =
                                metadataReader.getAnnotationMetadata().hasAnnotation(Component.class.getName());
                        // 是否有 @Component 派生注解
                        boolean hasMetaAnnotation =
                                metadataReader.getAnnotationMetadata().hasMetaAnnotation(Component.class.getName());
                        if (hasAnnotation || hasMetaAnnotation) {
                            // 生成 Bean 定义
                            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                                    .genericBeanDefinition(className)
                                    .getBeanDefinition();
                            if (configurableListableBeanFactory instanceof DefaultListableBeanFactory beanFactory) {
                                // 生成 Bean 名称
                                String beanName =
                                        beanNameGenerator.generateBeanName(beanDefinition, beanFactory);
                                // 注册到 Bean 工厂
                                beanFactory.registerBeanDefinition(beanName, beanDefinition);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("postProcessBeanFactory error", e);
        }
    }
}
