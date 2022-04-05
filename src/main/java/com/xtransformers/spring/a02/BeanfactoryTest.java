package com.xtransformers.spring.a02;

import java.util.Collection;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author daniel
 * @date 2022-04-01
 */
public class BeanfactoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanfactoryTest.class);

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // Bean 的定义
        AbstractBeanDefinition beanDefinition =
                BeanDefinitionBuilder.genericBeanDefinition(Config.class)
                        .setScope("singleton").getBeanDefinition();
        beanFactory.registerBeanDefinition("config", beanDefinition);

        // 解析 @Configuration @Bean
        // 给 BeanFactory 添加一些常用的后处理器
        // 工具类调用注册方法时，会设置默认的比较器
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
        // CommonAnnotationBeanPostProcessor
        // setOrder(Ordered.LOWEST_PRECEDENCE - 3);
        // AutowiredAnnotationBeanPostProcessor
        // private int order = Ordered.LOWEST_PRECEDENCE - 2;
        // 通过 Ordered 接口定义方法 int getOrder() 来排序的，Order 数字越小，优先级越高
        // CommonAnnotationBeanPostProcessor 优先级高于 AutowiredAnnotationBeanPostProcessor

        /*
        org.springframework.context.annotation.internalConfigurationAnnotationProcessor
        org.springframework.context.annotation.internalAutowiredAnnotationProcessor
        org.springframework.context.annotation.internalCommonAnnotationProcessor
        org.springframework.context.event.internalEventListenerProcessor
        org.springframework.context.event.internalEventListenerFactory
         */
        // BeanFactory 后处理器，补充 Bean 定义
        beanFactory.getBeansOfType(BeanFactoryPostProcessor.class).values()
                .forEach(beanFactoryPostProcessor -> beanFactoryPostProcessor.postProcessBeanFactory(beanFactory));

        for (String name : beanFactory.getBeanDefinitionNames()) {
            LOGGER.info(name);
        }

        // Bean 后处理器，针对 Bean 的生命周期各个阶段提供扩展
        // 处理 @Autowired
        // org.springframework.context.annotation.internalAutowiredAnnotationProcessor
        // 处理 @Resource
        // org.springframework.context.annotation.internalCommonAnnotationProcessor

        Collection<BeanPostProcessor> beanPostProcessors = beanFactory.getBeansOfType(BeanPostProcessor.class).values();
        // 以下两种写法都是可以的
        // 增加比较器之后，CommonAnnotationBeanPostProcessor 在 AutowiredAnnotationBeanPostProcessor 之前，所以结果是 Bean4
        beanPostProcessors.stream()
                .sorted(beanFactory.getDependencyComparator())
                .forEach(beanPostProcessor -> {
                    // 把 Bean 后处理器加入顺序打印出来
                    LOGGER.info("beanPostProcessor >>>>>>>>>>> " + beanPostProcessor);
                    beanFactory.addBeanPostProcessor(beanPostProcessor);
                });
        // beanFactory.addBeanPostProcessors(beanPostProcessors);

        // 默认情况下，懒汉式加载Bean 对象
        // 可以显示使用 饿汉式加载 Bean 对象
        beanFactory.preInstantiateSingletons();
        LOGGER.info("breakline >>>>>>>>>>>>");

        LOGGER.info("" + beanFactory.getBean(Bean1.class).getBean2());
        LOGGER.info("" + beanFactory.getBean(Bean1.class).getInter());
        /**
         * BeanFactory 不会做
         * 1. 不会主动调用 BeanFactory 后处理器
         * 2. 不会主动添加 Bean 后处理器
         * 3. 不会主动初始化单例
         * 4. 不会解析 BeanFactory
         * 5. 不会解析 ${} #{}
         *
         * BeanFactory 后处理器会有排序的逻辑
         */
    }

    @Configuration
    static class Config {

        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }

        @Bean
        public Bean3 bean3() {
            return new Bean3();
        }

        @Bean
        public Bean4 bean4() {
            return new Bean4();
        }
    }

    static class Bean1 {
        private static final Logger LOGGER = LoggerFactory.getLogger(Bean1.class);

        public Bean1() {
            LOGGER.debug("bean1 constructor");
        }

        @Autowired
        private Bean2 bean2;

        public Bean2 getBean2() {
            return bean2;
        }

        /**
         * 两个注解都有的情况下，而且还有冲突时，以 Bean 后处理器加入顺序决定
         * 默认情况下是 Bean3
         */
        @Autowired
        @Resource(name = "bean4")
        private Inter bean3;

        public Inter getInter() {
            return bean3;
        }
    }

    static class Bean2 {
        private static final Logger LOGGER = LoggerFactory.getLogger(Bean2.class);

        public Bean2() {
            LOGGER.debug("bean2 constructor");
        }
    }

    interface Inter {

    }

    static class Bean3 implements Inter {
        private static final Logger LOGGER = LoggerFactory.getLogger(Bean3.class);

        public Bean3() {
            LOGGER.debug("bean3 constructor");
        }
    }

    static class Bean4 implements Inter {
        private static final Logger LOGGER = LoggerFactory.getLogger(Bean4.class);

        public Bean4() {
            LOGGER.debug("bean4 constructor");
        }
    }
}
