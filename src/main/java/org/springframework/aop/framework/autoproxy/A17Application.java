package org.springframework.aop.framework.autoproxy;

import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
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
        // BeanPostProcessor 子类
        // 创建 -> (*) 依赖注入 -> 初始化 (*)
        /**
         * 2个重要方法
         * @see AnnotationAwareAspectJAutoProxyCreator#findEligibleAdvisors(Class, String)
         *      找到有【资格】的 Advisors
         *      一部分是低级的，可以由自己编写，如 advisor3
         *      一部分是高级的，由 @Aspect 注解解析出来的
         * @see AnnotationAwareAspectJAutoProxyCreator#wrapIfNecessary(Object, String, Object)
         *      内部调用 findEligibleAdvisors，只要返回集合不为空，则表示需要创建代理。
         */
        context.registerBean(AnnotationAwareAspectJAutoProxyCreator.class);

        context.refresh();
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            LOGGER.info("beanDefinitionName -> {}", beanDefinitionName);
        }

        AnnotationAwareAspectJAutoProxyCreator creator = context.getBean(AnnotationAwareAspectJAutoProxyCreator.class);
        List<Advisor> advisors = creator.findEligibleAdvisors(Target1.class, "target1");
        for (Advisor advisor : advisors) {
            LOGGER.info("advisor = {}", advisor);
        }
        /**
         * advisor = org.springframework.aop.interceptor.ExposeInvocationInterceptor.ADVISOR
         * advisor = org.springframework.aop.support.DefaultPointcutAdvisor: pointcut [AspectJExpressionPointcut: () execution(* foo())]; advice [org.springframework.aop.framework.autoproxy.A17Application$Configure$$Lambda$109/0x0000000800cfedc8@353352b6]
         * advisor = InstantiationModelAwarePointcutAdvisor: expression [execution(* foo())]; advice method [public void org.springframework.aop.framework.autoproxy.A17Application$Aspect1.before()]; perClauseKind=SINGLETON
         * advisor = InstantiationModelAwarePointcutAdvisor: expression [execution(* foo())]; advice method [public void org.springframework.aop.framework.autoproxy.A17Application$Aspect1.after()]; perClauseKind=SINGLETON
         */
        LOGGER.info("**************");
        advisors = creator.findEligibleAdvisors(Target2.class, "target2");
        for (Advisor advisor : advisors) {
            LOGGER.info("advisor = {}", advisor);
        }

        Object o1 = creator.wrapIfNecessary(new Target1(), "target1", "target1");
        LOGGER.info("o1 {}", o1.getClass());
        Object o2 = creator.wrapIfNecessary(new Target2(), "target2", "target2");
        LOGGER.info("o2 {}", o2.getClass());
        /**
         * o1 class org.springframework.aop.framework.autoproxy.A17Application$Target1$$EnhancerBySpringCGLIB$$4b660a22
         * o2 class org.springframework.aop.framework.autoproxy.A17Application$Target2
         */

        ((Target1) o1).foo();
        /**
         * advisor3.before
         * Aspect1.before
         * Target1.foo
         * Aspect1.after
         * advisor3.after
         */

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
