package com.xtransformers.spring.a14;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import org.springframework.cglib.proxy.MethodInterceptor;

/**
 * 手动实现Cglib代理类
 *
 * @author daniel
 * @date 2022-04-19
 */
public class Proxy extends Target {

    private MethodInterceptor methodInterceptor;

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    static Method save0;
    static Method save1;
    static Method save2;

    static {
        try {
            save0 = Target.class.getMethod("save");
            save1 = Target.class.getMethod("save", int.class);
            save2 = Target.class.getMethod("save", long.class);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    @Override
    public void save() {
        try {
            methodInterceptor.intercept(this, save0, new Object[0], null);
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override
    public void save(int i) {
        try {
            methodInterceptor.intercept(this, save1, new Object[]{i}, null);
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override
    public void save(long l) {
        try {
            methodInterceptor.intercept(this, save2, new Object[]{l}, null);
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }
}
