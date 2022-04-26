package org.springframework.aop.framework.autoproxy;

import javax.annotation.PostConstruct;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author daniel
 * @date 2022-04-26
 */
public class A17Application2 {

    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.registerBean(Config.class);
        context.refresh();
        context.close();
    }

    @Configuration
    static class Config {
        // 解析 @Aspect
        // 产生代理
        @Bean
        public AnnotationAwareAspectJAutoProxyCreator annotationAwareAspectJAutoProxyCreator() {
            return new AnnotationAwareAspectJAutoProxyCreator();
        }

        // 解析 @Autowired
        @Bean
        public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor() {
            return new AutowiredAnnotationBeanPostProcessor();
        }

        // 解析 @PostConstruct
        @Bean
        public CommonAnnotationBeanPostProcessor commonAnnotationBeanPostProcessor() {
            return new CommonAnnotationBeanPostProcessor();
        }

        @Bean
        public Advisor advisor(MethodInterceptor advice) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("execution(* foo())");
            return new DefaultPointcutAdvisor(pointcut, advice);
        }

        @Bean
        public MethodInterceptor advice() {
            return invocation -> {
                System.out.println("before");
                return invocation.proceed();
            };
        }

        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }
    }

    static class Bean1 {
        public Bean1() {
            System.out.println("Bean1()");
        }

        public void foo() {
            System.out.println("Bean1.foo()");
        }

        @Autowired
        public void setBean2(Bean2 bean2) {
            System.out.println("Bean1.setBean2() class: " + bean2.getClass());
        }

        @PostConstruct
        public void init() {
            System.out.println("Bean1.init()");
        }
    }

    /**
     * 注释掉 87 - 90 行代码日志
     * Bean1()
     * Bean1.init()
     * [TRACE] 00:01:10.490 [main] o.s.a.a.a.AnnotationAwareAspectJAutoProxyCreator - Creating implicit proxy for bean 'bean1' with 0 common interceptors and 2 specific interceptors
     * Bean2()
     * Bean2.setBean1() class is: class org.springframework.aop.framework.autoproxy.A17Application2$Bean1$$EnhancerBySpringCGLIB$$a97fd622
     * Bean2.init()
     *
     * 打开 87 - 90 行代码日志
     * Bean1()
     * Bean2()
     * [TRACE] 00:05:34.151 [main] o.s.a.a.a.AnnotationAwareAspectJAutoProxyCreator - Creating implicit proxy for bean 'bean1' with 0 common interceptors and 2 specific interceptors
     * Bean2.setBean1() class is: class org.springframework.aop.framework.autoproxy.A17Application2$Bean1$$EnhancerBySpringCGLIB$$879b69be
     * Bean2.init()
     * Bean1.setBean2() class: class org.springframework.aop.framework.autoproxy.A17Application2$Bean2
     * Bean1.init()
     */
    /**
     * 解读： 2种创建代理对象时机
     * 注释代码，只有 Bean2 依赖 Bean1
     * ①先执行 Bean1 的构造方法创建对象
     * ②紧接着执行了 init() 方法
     * ③之后创建了 Bean1 的代理对象
     * 所以代理对象是在初始化方法之后创建的！
     * ①Bean2 构造方法创建对象
     * ②Bean2 依赖 Bean1 注入
     * ③Bean2 初始化方法执行
     *
     * 对于 Bean1 (*) 代表创建代理对象
     * 创建 -> 依赖注入 -> 初始化 (*)
     *
     * 打开被注释的代码
     * Bean1 与 Bean2 循环依赖
     * ①Bean1 构造 创建对象
     * 因为 Bean1 依赖 Bean2，容器还没有 Bean2，进入 Bean2 流程
     * ①Bean2 构造 创建对象
     * ②创建 Bean1 代理对象
     * ②Bean2 依赖 Bean1 注入 需要一个代理的 Bean1，所以在此之前，需要先创建 Bean1 的代理对象
     * ③Bean2 初始化
     * 又回到 Bean1 的流程
     * ③Bean1 依赖注入 Bean2，因Bean2 中没有切面表达式中方法，所以不需要创建代理对象
     * ④Bean1 初始化
     *
     * 对于 Bean1 (*) 代表创建代理对象
     * 创建 -> (*) 依赖注入 -> 初始化
     *
     */

    /**
     * 代理对象的创建时机
     * 1. 初始化之后（无循环依赖时）
     * 2. 依赖注入之前（有循环依赖时），并暂存于二级缓存
     *
     * 依赖注入与初始化时，不应该被增强，仍应该被施加于原始对象
     * 依赖注入和初始化时，认为 Bean 还未被准备好，所以使用的是原始对象调用这2个方法
     */

    static class Bean2 {
        public Bean2() {
            System.out.println("Bean2()");
        }

        @Autowired
        public void setBean1(Bean1 bean1) {
            System.out.println("Bean2.setBean1() class is: " + bean1.getClass());
        }

        @PostConstruct
        public void init() {
            System.out.println("Bean2.init()");
        }
    }
}
