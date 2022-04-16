package com.xtransformers.spring.a12;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * @author daniel
 * @date 2022-04-16
 */
public class CglibProxyDemo {

    // 抛异常
    // static final class Target {
    static class Target {
        // 虽然不报错，但没有增强
        // public final void foo() {
        public void foo() {
            System.out.println("Target.foo");
        }
    }

    public static void main(String[] args) {
        /**
         * 1. 目标是父类型，代理是子类型
         * 2. 目标类型不能是 final 的，否则报错 Exception in thread "main" java.lang.IllegalArgumentException: Cannot subclass
         * final class com.xtransformers.spring.a12.CglibProxyDemo$Target
         * 3. 目标类型的增强方法，不能是 final 的，虽然不报错，但不会被增强，因为本质是通过子类重写父类方法实现的，而 final 方法不能被重写
         */
        // 目标对象
        Target target = new Target();

        // 生成代理对象
        Target proxy = (Target) Enhancer.create(Target.class, (MethodInterceptor) (p, method, args1, methodProxy) -> {
            System.out.println("before...");
            // 有3种方式实现
            // 1. 反射调用，需要目标对象
            // Object result = method.invoke(target, args1);
            // 2. 没有使用反射，需要目标对象(Spring使用方式)
            Object result = methodProxy.invoke(target, args1);
            // 3. 没有使用反射，不需要目标对象，只需要代理对象
            // Object result = methodProxy.invokeSuper(p, args1);
            System.out.println("after...");
            return result;
        });
        proxy.foo();
    }
}
