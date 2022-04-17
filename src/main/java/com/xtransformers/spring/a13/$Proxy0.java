package com.xtransformers.spring.a13;

import com.xtransformers.spring.a13.A13Main.Foo;
// import com.xtransformers.spring.a13.A13Main.InvocationHandler;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * 代理对象
 *
 * @author daniel
 * @date 2022-04-17
 */
public class $Proxy0 extends Proxy implements Foo {

    // private final InvocationHandler h;

    private static final Method FOO;
    private static final Method BAR;

    static {
        try {
            FOO = Foo.class.getMethod("foo");
            BAR = Foo.class.getMethod("bar");
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    public $Proxy0(InvocationHandler h) {
        // this.h = h;
        super(h);
    }

    @Override
    public void foo() {
        try {
            h.invoke(this, FOO, new Object[0]);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable throwable) {
            throw new UndeclaredThrowableException(throwable);
        }
    }

    @Override
    public int bar() {
        try {
            Object result = h.invoke(this, BAR, new Object[0]);
            return (int) result;
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable throwable) {
            throw new UndeclaredThrowableException(throwable);
        }
    }
}
