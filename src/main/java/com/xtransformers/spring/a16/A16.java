package com.xtransformers.spring.a16;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author daniel
 * @date 2022-04-22
 */
public class A16 {

    public static void main(String[] args) throws NoSuchMethodException {
        AspectJExpressionPointcut ep1 = new AspectJExpressionPointcut();
        ep1.setExpression("execution(* bar())");
        System.out.println("ep1.matches(foo()) = " + ep1.matches(T1.class.getMethod("foo"), T1.class));
        System.out.println("ep1.matches(ba()) = " + ep1.matches(T1.class.getMethod("bar"), T1.class));

        AspectJExpressionPointcut ep2 = new AspectJExpressionPointcut();
        ep2.setExpression("@annotation(org.springframework.transaction.annotation.Transactional)");
        System.out.println("ep2.matches(foo()) = " + ep2.matches(T1.class.getMethod("foo"), T1.class));
        System.out.println("ep2.matches(ba()) = " + ep2.matches(T1.class.getMethod("bar"), T1.class));

        /**
         * ep1.matches(foo()) = false
         * ep1.matches(ba()) = true
         * ep2.matches(foo()) = true
         * ep2.matches(ba()) = false
         */
    }

    static class T1 {
        @Transactional
        public void foo() {
        }

        public void bar() {
        }
    }
}
