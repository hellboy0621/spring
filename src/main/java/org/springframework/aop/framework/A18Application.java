package org.springframework.aop.framework;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectInstanceFactory;
import org.springframework.aop.aspectj.AspectJAfterReturningAdvice;
import org.springframework.aop.aspectj.AspectJAroundAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.AspectJMethodBeforeAdvice;
import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * @author daniel
 * @date 2022-04-28
 */
public class A18Application {

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
        public void around(ProceedingJoinPoint pjp) throws Throwable {
            System.out.println("Aspect.around 1");
            pjp.proceed();
            System.out.println("Aspect.around 2");
        }
    }

    public static void main(String[] args) throws Throwable {
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
            } else if (method.isAnnotationPresent(AfterReturning.class)) {
                String expression = method.getAnnotation(AfterReturning.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                AspectJAfterReturningAdvice advice = new AspectJAfterReturningAdvice(method, pointcut, factory);
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisorList.add(advisor);
            } else if (method.isAnnotationPresent(Around.class)) {
                String expression = method.getAnnotation(Around.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                AspectJAroundAdvice advice = new AspectJAroundAdvice(method, pointcut, factory);
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
         * AspectJAfterAdvice           (implements MethodInterceptor)
         * AspectJAfterReturningAdvice
         * AspectJAfterThrowingAdvice   (implements MethodInterceptor)
         * AspectJAroundAdvice          (implements MethodInterceptor)
         *
         * 已经实现了 MethodInterceptor 接口的，无需再转换了
         */

        /**
         * 通知统一转换为环绕通知
         * @see org.aopalliance.intercept.MethodInterceptor
         *
         * 无论 ProxyFactory 基于哪种方式创建代理，最后干活（调用 advice）的是一个 MethodInterceptor 对象
         * a. 因为 advisor 有多个，且一个套一个调用，因此需要一个调用链对象，即 MethodInvocation
         * b. MethodInvocation 要知道 advice 有哪些，还要知道目标，调用次序如下
         *
         *      将 MethodInvocation 放入当前线程
         *      | -> before1 -----------------------------------| 从当前线程获取 MethodInvocation
         *      |                                               |
         *      |   | -> before2 -------------------|           | 从当前线程获取 MethodInvocation
         *      |   |                               |           |
         *      |   |   | -> target --- 目标 --- advice2 --- advice1
         *      |   |                               |           |
         *      |   | -> after2  -------------------|           |
         *      |                                               |
         *      | -> after1  -----------------------------------|
         * c. 从上图看出，环绕通知才适合作为 advice，因此其他 before、afterReturning 都会转换成环绕通知
         * d. 统一转换为环绕通知，适配器模式
         *      - 对外是为了方便使用，要区分 before、afterReturning
         *      - 对内统一都是环绕通知，统一用 MethodInterceptor 表示
         *
         */

        // 通知统一转换为环绕通知
        ProxyFactory proxyFactory = new ProxyFactory();
        Target target = new Target();
        proxyFactory.setTarget(target);
        // 把 MethodInvocation 放入当前线程
        // Exception in thread "main" java.lang.IllegalStateException:
        // No MethodInvocation found: Check that an AOP invocation is in progress and that the ExposeInvocationInterceptor is upfront in the interceptor chain.
        // Specifically, note that advices with order HIGHEST_PRECEDENCE will execute before ExposeInvocationInterceptor!
        // In addition, ExposeInvocationInterceptor and ExposeInvocationInterceptor.currentInvocation() must be invoked from the same thread.
        proxyFactory.addAdvice(ExposeInvocationInterceptor.INSTANCE);
        proxyFactory.addAdvisors(advisorList);

        System.out.println(">>>>>>>>>>>>>>>");

        List<Object> methodInterceptorList =
                proxyFactory.getInterceptorsAndDynamicInterceptionAdvice(Target.class.getMethod("foo"), Target.class);
        for (Object each : methodInterceptorList) {
            System.out.println("each = " + each);
        }

        /**
         * 打印日志中，可以看到转换前后类型
         * AspectJMethodBeforeAdvice -> MethodBeforeAdviceInterceptor
         * AspectJAroundAdvice（没有转换）
         * AspectJAfterReturningAdvice -> AfterReturningAdviceInterceptor
         */

        /**
         * 适配器模式
         * @see org.springframework.aop.framework.adapter.MethodBeforeAdviceAdapter
         * MethodBeforeAdviceAdapter    将   @Before AspectJMethodBeforeAdvice              适配为 MethodBeforeAdviceInterceptor
         * @see org.springframework.aop.framework.adapter.AfterReturningAdviceAdapter
         * AfterReturningAdviceAdapter  将   @AfterReturning AspectJAfterReturningAdvice    适配为 AfterReturningAdviceInterceptor
         *
         */

        System.out.println(">>>>>>>>>>>>>>>");

        // 创建并执行调用链（环绕通知 + 目标）
        MethodInvocation methodInvocation = new ReflectiveMethodInvocation(
                null, target, Target.class.getMethod("foo"), new Object[0], Target.class, methodInterceptorList
        );
        methodInvocation.proceed();

        /**
         * 此步模拟调用链过程，是一个简单的递归过程
         * 1. proceed() 方法调用链中下一个环绕通知
         * 2. 每个环绕通知内部继续调用 proceed()
         * 3. 调用到没有更多通知了，就调用目标方法
         */
    }

    static class Target {
        public void foo() {
            System.out.println("Target.foo");
        }
    }
}
