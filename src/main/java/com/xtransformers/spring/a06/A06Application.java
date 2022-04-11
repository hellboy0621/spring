package com.xtransformers.spring.a06;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Aware 接口
 * InitializingBean 接口
 *
 * @author daniel
 * @date 2022-04-10
 */
public class A06Application {

    public static void main(String[] args) {
        /**
         * 1. Aware 接口用于注入一些与容器相关的信息
         *   a. BeanNameAware                   public void setBeanName(String name)
         *   b. BeanFactoryAware                public void setBeanFactory(BeanFactory beanFactory)
         *   c. ApplicationContextAware         public void setApplicationContext(ApplicationContext applicationContext)
         *   d. EmbeddedValueResolverAware      public void setEmbeddedValueResolver(StringValueResolver resolver)
         */

        GenericApplicationContext context = new GenericApplicationContext();

        // context.registerBean("myBean", MyBean.class);

        /**
         * 2. b、c、d 的功能用 @Autowired 就能实现，为什么还要使用 aware 接口？
         *   a. @Autowired 的解析需要用到 bean 后处理器，属于扩展功能
         *   b. Aware 接口属于内置功能，不加任何扩展，Spring 就能识别
         * 某些情况下，扩展功能会失效，而内置功能不会失效
         */
        context.registerBean(AutowiredAnnotationBeanPostProcessor.class);
        context.registerBean(CommonAnnotationBeanPostProcessor.class);

        context.registerBean("config1", Config1.class);
        context.registerBean(ConfigurationClassPostProcessor.class);

        context.refresh();
        // 1. 找到所有的 BeanFactory 后处理器
        // 2. 添加 Bean 后处理器
        // 3. 初始化单例

        context.close();
    }
}
