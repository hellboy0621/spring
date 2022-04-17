package com.xtransformers.spring.a13;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 自行实现 JDK 动态代理
 *
 * @author daniel
 * @date 2022-04-17
 */
public class A13Main {

    interface Foo {
        void foo();

        int bar();
    }

    /**
     * 目标对象
     */
    static final class Target implements Foo {
        @Override
        public void foo() {
            System.out.println("Target.foo");
        }

        @Override
        public int bar() {
            System.out.println("Target.bar");
            return 1000;
        }
    }

    // interface InvocationHandler {
    //     Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
    // }

    public static void main(String[] args) {
        // 代理对象
        Foo proxy = new $Proxy0(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 1. 功能增强
                System.out.println("before ...");
                // 2. 调用目标对象方法
                // new Target().foo();
                return method.invoke(new Target(), args);
            }
        });
        proxy.foo();
        proxy.bar();
    }
}
