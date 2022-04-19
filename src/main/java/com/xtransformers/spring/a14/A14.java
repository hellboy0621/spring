package com.xtransformers.spring.a14;

import java.lang.reflect.Method;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

/**
 * @author daniel
 * @date 2022-04-19
 */
public class A14 {

    public static void main(String[] args) {
        Proxy proxy = new Proxy();
        Target target = new Target();
        proxy.setMethodInterceptor(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy)
                    throws Throwable {
                System.out.println("before ...");
                Object result = method.invoke(target, args);
                System.out.println("after ...");
                return result;
            }
        });
        proxy.save();
        proxy.save(1);
        proxy.save(2L);
    }
}
