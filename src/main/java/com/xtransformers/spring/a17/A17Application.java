package com.xtransformers.spring.a17;

import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author daniel
 * @date 2022-04-25
 */
public class A17Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(A17Application.class);

    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("aspect1", Aspect1.class);
        context.registerBean("configure", Configure.class);
        // 解析 @Configuration
        context.registerBean(ConfigurationClassPostProcessor.class);

        context.refresh();
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            LOGGER.info("beanDefinitionName -> {}", beanDefinitionName);
        }
        context.close();
    }

    static class Target1 {
        public void foo() {
            System.out.println("Target1.foo");
        }
    }

    static class Target2 {
        public void bar() {
            System.out.println("Target2.bar");
        }
    }

    // 高级的切面
    @Aspect
    static class Aspect1 {

        @Before("execution(* foo())")
        public void before() {
            System.out.println("Aspect1.before");
        }

        @After("execution(* foo())")
        public void after() {
            System.out.println("Aspect1.after");
        }
    }

    // 低级的切面
    @Configuration
    static class Configure {
        /**
         * 低级切面
         *
         * @param advice3
         * @return
         */
        @Bean
        public Advisor advisor3(MethodInterceptor advice3) {
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("execution(* foo())");
            return new DefaultPointcutAdvisor(pointcut, advice3);
        }

        /**
         * 环绕通知
         *
         * @return
         */
        @Bean
        public MethodInterceptor advice3() {
            return invocation -> {
                System.out.println("advisor3.before");
                Object result = invocation.proceed();
                System.out.println("advisor3.after");
                return result;
            };
        }
    }
}
