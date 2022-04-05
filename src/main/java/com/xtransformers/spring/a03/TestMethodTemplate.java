package com.xtransformers.spring.a03;

import java.util.ArrayList;
import java.util.List;

/**
 * @author daniel
 * @date 2022-04-05
 */
public class TestMethodTemplate {

    public static void main(String[] args) {
        // 使用模板方法前，需要硬编码，不利于功能扩展
        MyBeanFactory beanFactory = new MyBeanFactory();
        beanFactory.getBean();

        System.out.println("********");

        // 使用模板方法模式后，易于扩展
        BeanFactoryTest beanFactoryTest = new BeanFactoryTest();
        beanFactoryTest.addBeanPostProcessor(obj ->
                System.out.println("BeanPostProcessor -> inject 解析 @Autowired"));
        beanFactoryTest.addBeanPostProcessor(obj ->
                System.out.println("BeanPostProcessor -> inject 解析 @Resource"));
        beanFactoryTest.getBean();
    }


    static class MyBeanFactory {
        public Object getBean() {
            Object bean = new Object();
            System.out.println("构造 " + bean);
            // @Autowired @Resource
            System.out.println("依赖注入 " + bean);
            System.out.println("初始化 " + bean);
            return bean;
        }
    }

    // 模板方法 Template Method Pattern
    // 模板方法中，有静态不变的部分，有动态变化的部分
    static class BeanFactoryTest {
        private static final List<BeanPostProcessor> postProcessors = new ArrayList<>();

        public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
            postProcessors.add(beanPostProcessor);
        }

        public Object getBean() {
            Object bean = new Object();
            System.out.println("构造 " + bean);
            // @Autowired @Resource
            System.out.println("依赖注入 " + bean);
            for (BeanPostProcessor postProcessor : postProcessors) {
                postProcessor.inject(bean);
            }
            System.out.println("初始化 " + bean);
            return bean;
        }
    }

    static interface BeanPostProcessor {
        // 处理依赖注入
        void inject(Object obj);
    }
}
