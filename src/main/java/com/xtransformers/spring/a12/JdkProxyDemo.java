package com.xtransformers.spring.a12;

import java.lang.reflect.Proxy;

/**
 * @author daniel
 * @date 2022-04-16
 */
public class JdkProxyDemo {

    interface Foo {
        void foo();
    }

    /**
     * 目标对象
     */
    static final class Target implements Foo {
        @Override
        public void foo() {
            System.out.println("Target.foo");
        }
    }

    public static void main(String[] args) {
        /**
         * 1. 代理对象与目标对象是兄弟关系，实现相同的接口
         * 2. 目标对象可以是 final
         */
        // 目标对象
        Target target = new Target();

        // 生成代理对象
        ClassLoader loader = JdkProxyDemo.class.getClassLoader();
        Foo proxy = (Foo) Proxy.newProxyInstance(loader, new Class[]{Foo.class}, (p, method, args1) -> {
            System.out.println("before...");
            Object result = method.invoke(target, args1);
            System.out.println("after...");
            return result;
        });
        proxy.foo();
    }
}
