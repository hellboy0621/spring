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
            public Object intercept(Object p, Method method, Object[] args, MethodProxy methodProxy)
                    throws Throwable {
                System.out.println("before ...");
                // 反射调用
                // Object result = method.invoke(target, args);

                // 内部无反射，基于目标用
                // Object result = methodProxy.invoke(target, args);

                // 内部无反射，基于代理用
                Object result = methodProxy.invokeSuper(p, args);

                System.out.println("after ...");
                return result;
            }
        });
        proxy.save();
        proxy.save(1);
        proxy.save(2L);
    }
}
