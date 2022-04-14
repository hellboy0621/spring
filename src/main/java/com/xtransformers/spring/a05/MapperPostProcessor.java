package com.xtransformers.spring.a05;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

/**
 * 自定义BeanFactory后处理器，解析 Mapper
 *
 * @author daniel
 * @date 2022-04-10
 */
public class MapperPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperPostProcessor.class);

    /**
     * 之前都是实现 BeanFactoryPostProcessor
     * 覆写方法 postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
     * 其中方法参数 ConfigurableListableBeanFactory 里面没有 registerBeanDefinition 这个方法
     * void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
     * 需要强转
     *
     * DefaultListableBeanFactory 实现了 BeanDefinitionRegistry 接口 获得的 registerBeanDefinition 能力
     * 所以这里实现 BeanDefinitionRegistryPostProcessor
     */

    /**
     * 在这里模拟通过 @Bean 实现对 Mapper 的解析
     *
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanFactory) throws BeansException {
        // 类似 ComponentScan 把 Mapper 所在包扫描后组装为 MapperFactoryBean<Mapper1>
        // 1. 获取 Mapper 所在包下所有 *.class 资源
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:com/xtransformers/spring/a05/mapper/**/*.class");

            // 2. 遍历，组装 BeanDefinition 并注册到 BeanFactory 中
            CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
            AnnotationBeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
            for (Resource resource : resources) {
                // 获取接口类型资源
                MetadataReader reader = factory.getMetadataReader(resource);
                ClassMetadata classMetadata = reader.getClassMetadata();
                if (classMetadata.isInterface()) {
                    // 目标
                    // MapperFactoryBean<Mapper1> mapperFactoryBean = new MapperFactoryBean<>(Mapper1.class);
                    // mapperFactoryBean.setSqlSessionFactory(sqlSessionFactory);
                    // AbstractBeanDefinition beanDefinition =
                    //         BeanDefinitionBuilder.genericBeanDefinition(MapperFactoryBean.class)
                    //                 // 构造方法需要传入 Mapper class
                    //                 .addConstructorArgValue(classMetadata.getClassName())
                    //                 // 需要设置 sqlSessionFactory，通过自动注入 因为在 Config 中通过 @Bean 提供了
                    //                 .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
                    //                 .getBeanDefinition();
                    // 错误写法，如果通过 beanDefinition 获取 beanName，每次循环得到的都是 mapperFactoryBean
                    // String beanName = beanNameGenerator.generateBeanName(beanDefinition, beanFactory);
                    // LOGGER.info("beanName : {}", beanName);
                    // AbstractBeanDefinition beanDefinitionForBeanName =
                    //         BeanDefinitionBuilder.genericBeanDefinition(classMetadata.getClassName())
                    //                 .getBeanDefinition();
                    // String beanName = beanNameGenerator.generateBeanName(beanDefinitionForBeanName, beanFactory);
                    // beanFactory.registerBeanDefinition(beanName, beanDefinition);
                }
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
