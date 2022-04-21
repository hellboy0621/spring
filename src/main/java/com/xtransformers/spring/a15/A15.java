package com.xtransformers.spring.a15;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * @author daniel
 * @date 2022-04-22
 */
public class A15 {
    /**
     * 两个切面概念
     * <br/>
     * aspect
     * <br/>
     * 通知1(advice) + 切点1(pointcut)
     * <br/>
     * 通知2(advice) + 切点2(pointcut)
     * <br/>
     * <br/>
     *
     * advisor
     * <br/>
     * 更细粒度的切面
     * <br/>
     * 包含一个通知和一个切点
     */

    public static void main(String[] args) {
        // 切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* foo())");

        // 通知
        MethodInterceptor advice = new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                System.out.println("before...");
                Object result = invocation.proceed();
                System.out.println("after...");
                return result;
            }
        };

        // 切面
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);

        // 代理
        Target1 target1 = new Target1();
        ProxyFactory factory = new ProxyFactory();
        factory.addAdvisor(advisor);
        factory.setTarget(target1);

        I1 proxy = (I1) factory.getProxy();
        proxy.foo();
        proxy.bar();
        System.out.println("proxy.getClass() = " + proxy.getClass());

        /**
         * before...
         * Target1.foo
         * after...
         * Target1.bar
         * proxy.getClass() = class com.xtransformers.spring.a15.A15$Target1$$EnhancerBySpringCGLIB$$326fcd70
         */
    }

    static interface I1 {
        void foo();

        void bar();
    }

    static class Target1 implements I1 {

        @Override
        public void foo() {
            System.out.println("Target1.foo");
        }

        @Override
        public void bar() {
            System.out.println("Target1.bar");
        }
    }

    static class Target2 implements I1 {

        @Override
        public void foo() {
            System.out.println("Target2.foo");
        }

        @Override
        public void bar() {
            System.out.println("Target2.bar");
        }
    }
}
