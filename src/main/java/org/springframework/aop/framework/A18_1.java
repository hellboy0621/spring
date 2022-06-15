package org.springframework.aop.framework;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class A18_1 {

    // 目标类
    static class Target {
        public void foo() {
            System.out.println("Target.foo");
        }
    }

    // 通知类
    static class Advice1 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("Advice1.before");
            // 调用下一个通知或目标
            Object result = invocation.proceed();
            System.out.println("Advice1.after");
            return result;
        }
    }

    static class Advice2 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("Advice2.before");
            // 调用下一个通知或目标
            Object result = invocation.proceed();
            System.out.println("Advice2.after");
            return result;
        }
    }

    static class MyInvocation implements MethodInvocation {

        private Object target;
        private Method method;
        private Object[] args;
        List<MethodInterceptor> methodInterceptors;
        // 调用次数
        private int count = 1;

        public MyInvocation(Object target, Method method, Object[] args, List<MethodInterceptor> methodInterceptors) {
            this.target = target;
            this.method = method;
            this.args = args;
            this.methodInterceptors = methodInterceptors;
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public Object[] getArguments() {
            return args;
        }

        @Override
        public Object proceed() throws Throwable {
            // 责任链模式
            // 调用每一个环绕通知，调用目标
            if (count > methodInterceptors.size()) {
                // 调用目标，返回并结束递归
                return method.invoke(target, args);
            }
            // 逐一调用通知， count++
            MethodInterceptor methodInterceptor = methodInterceptors.get(count++ - 1);
            return methodInterceptor.invoke(this);
        }

        @Override
        public Object getThis() {
            return target;
        }

        @Override
        public AccessibleObject getStaticPart() {
            return method;
        }
    }

    public static void main(String[] args) throws Throwable {
        Target target = new Target();
        List<MethodInterceptor> methodInterceptors = List.of(
                new Advice1(),
                new Advice2()
        );
        MyInvocation myInvocation =
                new MyInvocation(target, Target.class.getMethod("foo"), new Object[0], methodInterceptors);
        myInvocation.proceed();
    }
}
