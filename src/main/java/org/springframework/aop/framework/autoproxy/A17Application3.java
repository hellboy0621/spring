package org.springframework.aop.framework.autoproxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.aop.aspectj.AspectJAfterAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJMethodBeforeAdvice;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * @author daniel
 * @date 2022-04-28
 */
public class A17Application3 {

    static class Aspect {

        @Before("execution(* foo())")
        public void before() {
            System.out.println("Aspect.before");
        }

        @Before("execution(* foo())")
        public void before2() {
            System.out.println("Aspect.before2");
        }

        @After("execution(* foo())")
        public void after() {
            System.out.println("Aspect.after");
        }

        @AfterReturning("execution(* foo())")
        public void afterReturning() {
            System.out.println("Aspect.afterReturning");
        }

        @AfterThrowing("execution(* foo())")
        public void afterThrowing() {
            System.out.println("Aspect.afterThrowing");
        }

        @Around("execution(* foo())")
        public void arount(ProceedingJoinPoint pjp) throws Throwable {
            System.out.println("Aspect.arount");
        }
    }

    public static void main(String[] args) {
        // 高级切面 Aspect 转换为低级切面 Advisor
        List<Advisor> advisorList = new ArrayList<>();
        AspectInstanceFactory factory = new SingletonAspectInstanceFactory(new Aspect());
        for (Method method : Aspect.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Before.class)) {
                // 解析切点
                String expression = method.getAnnotation(Before.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);

                // 解析通知 通知类
                AspectJMethodBeforeAdvice advice = new AspectJMethodBeforeAdvice(method, pointcut, factory);

                // 切面
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisorList.add(advisor);
            } else if (method.isAnnotationPresent(After.class)) {
                String expression = method.getAnnotation(After.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                AspectJAfterAdvice advice = new AspectJAfterAdvice(method, pointcut, factory);
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisorList.add(advisor);
            }
        }
        for (Advisor advisor : advisorList) {
            System.out.println("advisor = " + advisor);
        }
        /**
         * Before 前置通知，会被转换为下面原始的 AspectJMethodBeforeAdvice 形式，该对象包含了如下信息
         * 1. 通知代码从哪儿来
         * 2. 切点是什么
         * 3. 通知对象如何创建，本例共用同一个 Aspect 对象
         *
         * 类似的通知:
         * AspectJMethodBeforeAdvice
         * AspectJAfterAdvice
         * AspectJAfterReturningAdvice
         * AspectJAfterThrowingAdvice
         * AspectJAroundAdvice
         */

    }
}
