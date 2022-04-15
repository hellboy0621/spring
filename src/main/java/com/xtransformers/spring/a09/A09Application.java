package com.xtransformers.spring.a09;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author daniel
 * @date 2022-04-14
 */
@ComponentScan("com.xtransformers.spring.a09")
public class A09Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(A09Application.class);

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(A09Application.class);

        Element element = context.getBean(Element.class);
        // 以下代码 @Scope 注解的 BeanForPrototype prototype 失效
        LOGGER.info("element.beanForPrototype {}", element.getBeanForPrototype());
        LOGGER.info("element.beanForPrototype {}", element.getBeanForPrototype());
        LOGGER.info("element.beanForPrototype {}", element.getBeanForPrototype());

        LOGGER.info("beanForPrototype {}", element.getBeanForPrototype().getClass());

        /**
         * 21:46:27.485 [main] INFO com.xtransformers.spring.a09.A09Application - element.beanForPrototype com.xtransformers.spring.a09.BeanForPrototype@795cd85e
         * 21:46:27.489 [main] INFO com.xtransformers.spring.a09.A09Application - element.beanForPrototype com.xtransformers.spring.a09.BeanForPrototype@795cd85e
         * 21:46:27.491 [main] INFO com.xtransformers.spring.a09.A09Application - element.beanForPrototype com.xtransformers.spring.a09.BeanForPrototype@795cd85e
         * 21:46:27.491 [main] INFO com.xtransformers.spring.a09.A09Application - beanForPrototype class com.xtransformers.spring.a09.BeanForPrototype
         *
         * 以上日志可以看出，虽然 BeanForPrototype 是多例，但是注入的是同一个对象；
         * 对于单例对象来讲，依赖注入仅发生了一次，后续再没有用到多例的 BeanForPrototype
         * 因此，Element 用的始终是第一次依赖注入的 BeanForPrototype。
         *
         * 解决：
         * 1. 在被注入的属性上，使用 @Lazy 生成代理
         * 代理对象虽然还是同一个，但当每次使用代理对象的任意方法时，由代理创建新的 BeanForPrototype 对象
         *
         * 2. 在对象上，增加注解属性
         *
         *
         * 00:13:03.255 [main] INFO com.xtransformers.spring.a09.A09Application - element.beanForPrototype com.xtransformers.spring.a09.BeanForPrototype@37858383
         * 00:13:03.269 [main] INFO com.xtransformers.spring.a09.A09Application - element.beanForPrototype com.xtransformers.spring.a09.BeanForPrototype@5136d012
         * 00:13:03.270 [main] INFO com.xtransformers.spring.a09.A09Application - element.beanForPrototype com.xtransformers.spring.a09.BeanForPrototype@e1de817
         * 00:13:03.271 [main] INFO com.xtransformers.spring.a09.A09Application - beanForPrototype class com.xtransformers.spring.a09.BeanForPrototype$$EnhancerBySpringCGLIB$$b3c649c0
         */

        LOGGER.info("element.beanForPrototype2 {}", element.getBeanForPrototype2());
        LOGGER.info("element.beanForPrototype2 {}", element.getBeanForPrototype2());
        LOGGER.info("element.beanForPrototype2 {}", element.getBeanForPrototype2());

        LOGGER.info("beanForPrototype2 {}", element.getBeanForPrototype2().getClass());

        context.close();

    }
}
