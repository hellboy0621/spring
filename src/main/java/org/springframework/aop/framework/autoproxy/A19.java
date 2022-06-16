package org.springframework.aop.framework.autoproxy;

import java.lang.reflect.Field;
import java.util.List;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

public class A19 {

    @Aspect
    static class MyAspect {
        // 静态通知调用，不需要参数绑定，执行时不需要切点
        @Before("execution(* foo(..))")
        public void before1() {
            System.out.println("MyAspect.before1");
        }

        // 动态通知调用，需要参数绑定，执行时还需要切点对象
        @Before("execution(* foo(..)) && args(x)")
        public void before2(int x) {
            System.out.printf("MyAspect.before2 target foo(%d)%n", x);
        }
    }

    @Configuration
    static class MyConfig {
        @Bean
        AnnotationAwareAspectJAutoProxyCreator proxyCreator() {
            return new AnnotationAwareAspectJAutoProxyCreator();
        }

        @Bean
        public MyAspect myAspect() {
            return new MyAspect();
        }
    }

    public static void main(String[] args) throws Throwable {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.registerBean(MyConfig.class);
        context.refresh();

        AnnotationAwareAspectJAutoProxyCreator creator = context.getBean(AnnotationAwareAspectJAutoProxyCreator.class);
        List<Advisor> advisors = creator.findEligibleAdvisors(Target.class, "target");

        Target target = new Target();
        ProxyFactory factory = new ProxyFactory();
        factory.setTarget(target);
        factory.addAdvisors(advisors);
        Object proxy = factory.getProxy();

        List<Object> interceptors =
                factory.getInterceptorsAndDynamicInterceptionAdvice(Target.class.getMethod("foo", int.class),
                        Target.class);
        for (Object interceptor : interceptors) {
            System.out.println("interceptor = " + interceptor);
        }
        System.out.println("**********************");
        for (Object interceptor : interceptors) {
            showDetail(interceptor);
        }

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>");
        ReflectiveMethodInvocation invocation = new ReflectiveMethodInvocation(
                proxy, target, Target.class.getMethod("foo", int.class), new Object[]{250}, Target.class, interceptors
        ) {
        };
        invocation.proceed();
        /**
         * interceptor = org.springframework.aop.interceptor.ExposeInvocationInterceptor@5505ae1a
         * interceptor = org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor@73cd37c0
         * interceptor = org.springframework.aop.framework.InterceptorAndDynamicMethodMatcher@21337f7b
         *
         * InterceptorAndDynamicMethodMatcher
         * 为了支持动态通知调用
         * 类里面包含环绕通知 final MethodInterceptor interceptor;
         * 和切点 final MethodMatcher methodMatcher;
         *
         *
         * 普通环绕通知：org.springframework.aop.interceptor.ExposeInvocationInterceptor@5505ae1a
         * 普通环绕通知：org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor@73cd37c0
         * 环绕通知和切点：org.springframework.aop.framework.InterceptorAndDynamicMethodMatcher@21337f7b
         * 	 切点为：AspectJExpressionPointcut: (int x) execution(* foo(..)) && args(x)
         * 	 通知为：org.springframework.aop.framework.adapter.MethodBeforeAdviceInterceptor@60df60da
         * >>>>>>>>>>>>>>>>>>>>>>>>>>>
         * MyAspect.before1
         * MyAspect.before2 target foo(250)
         * Target.foo(250)
         */

        /**
         * a. 有参数绑定的通知调用时还需要切点，对参数进行匹配及绑定
         * b. 复杂程度高，性能比无参数绑定的通知调用低
         */

    }

    static class Target {
        public void foo(int i) {
            System.out.printf("Target.foo(%d)%n", i);
        }
    }

    public static void showDetail(Object o) {
        try {
            Class<?> clazz = Class.forName("org.springframework.aop.framework.InterceptorAndDynamicMethodMatcher");
            if (clazz.isInstance(o)) {
                Field methodMatcher = clazz.getDeclaredField("methodMatcher");
                methodMatcher.setAccessible(true);
                Field methodInterceptor = clazz.getDeclaredField("interceptor");
                methodInterceptor.setAccessible(true);
                System.out.println("环绕通知和切点：" + o);
                System.out.println("\t 切点为：" + methodMatcher.get(o));
                System.out.println("\t 通知为：" + methodInterceptor.get(o));
            } else {
                System.out.println("普通环绕通知：" + o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
