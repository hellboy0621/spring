package com.xtransformers.spring.a16;

import java.lang.reflect.Method;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author daniel
 * @date 2022-04-22
 */
public class A16 {

    public static void main(String[] args) throws NoSuchMethodException {
        AspectJExpressionPointcut ep1 = new AspectJExpressionPointcut();
        ep1.setExpression("execution(* bar())");
        System.out.println("ep1.matches(foo() T1) = " + ep1.matches(T1.class.getMethod("foo"), T1.class));
        System.out.println("ep1.matches(ba() T1) = " + ep1.matches(T1.class.getMethod("bar"), T1.class));

        AspectJExpressionPointcut ep2 = new AspectJExpressionPointcut();
        ep2.setExpression("@annotation(org.springframework.transaction.annotation.Transactional)");
        System.out.println("ep2.matches(foo() T1) = " + ep2.matches(T1.class.getMethod("foo"), T1.class));
        System.out.println("ep2.matches(ba() T1) = " + ep2.matches(T1.class.getMethod("bar"), T1.class));

        /**
         * ep1.matches(foo()) = false
         * ep1.matches(ba()) = true
         * ep2.matches(foo()) = true
         * ep2.matches(ba()) = false
         */
        /**
         * AspectJExpressionPointcut 只能识别方法名或方法上注解，不能识别类或接口上注解
         */

        StaticMethodMatcherPointcut pointcut = new StaticMethodMatcherPointcut() {
            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                // 检查方法上是否有 Transactional 注解
                MergedAnnotations annotations = MergedAnnotations.from(method);
                if (annotations.isPresent(Transactional.class)) {
                    return true;
                }

                // 检查类上是否有 Transactional 注解
                // 默认搜索策略为 SearchStrategy.DIRECT
                annotations = MergedAnnotations.from(targetClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);
                if (annotations.isPresent(Transactional.class)) {
                    return true;
                }
                return false;
            }
        };
        // 验证
        System.out.println("pointcut.matches(foo() T1) = " + pointcut.matches(T1.class.getMethod("foo"), T1.class));
        System.out.println("pointcut.matches(bar() T1) = " + pointcut.matches(T1.class.getMethod("bar"), T1.class));
        System.out.println("pointcut.matches(foo() T2) = " + pointcut.matches(T2.class.getMethod("foo"), T2.class));
        System.out.println("pointcut.matches(foo() T3) = " + pointcut.matches(T3.class.getMethod("foo"), T3.class));

        /**
         * pointcut.matches(foo() T1) = true
         * pointcut.matches(bar() T1) = false
         * pointcut.matches(foo() T2) = true
         * pointcut.matches(foo() T3) = true
         */
    }

    /**
     * 如下 @Transactional 3种使用情况
     * 1. 方法上
     * 2. 类上
     * 3. 接口上
     */
    static class T1 {
        @Transactional
        public void foo() {
        }

        public void bar() {
        }
    }

    @Transactional
    static class T2 {
        public void foo() {
        }
    }

    @Transactional
    static interface I3 {
        void foo();
    }

    static class T3 implements I3 {
        @Override
        public void foo() {
        }
    }

}
