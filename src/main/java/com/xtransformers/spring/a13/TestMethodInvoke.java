package com.xtransformers.spring.a13;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反编译后代码
 * https://foggy-airbus-610.notion.site/662faca6342d4b7f9f1b68d2da648db7
 *
 * @author daniel
 * @date 2022-04-19
 */
public class TestMethodInvoke {

    /**
     * 运行时请添加 --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/jdk.internal.reflect=ALL-UNNAMED
     *
     * 14:foo
     * 14:jdk.internal.reflect.NativeMethodAccessorImpl@6f496d9f
     * 15:foo
     * 15:jdk.internal.reflect.GeneratedMethodAccessor1@27bc2616
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Method fooMethod = TestMethodInvoke.class.getMethod("foo", int.class);
        for (int i = 0; i < 17; i++) {
            fooMethod.invoke(null, i);
            show(i, fooMethod);
        }
        System.in.read();
    }

    private static void show(int i, Method foo) throws Exception {
        Method getMethodAccessor = Method.class.getDeclaredMethod("getMethodAccessor");
        getMethodAccessor.setAccessible(true);
        Object invoke = getMethodAccessor.invoke(foo);
        if (invoke == null) {
            System.out.println(i + ":" + null);
            return;
        }
        Field delegate =
                Class.forName("jdk.internal.reflect.DelegatingMethodAccessorImpl").getDeclaredField("delegate");
        delegate.setAccessible(true);
        System.out.println(i + ":" + delegate.get(invoke));
    }

    public static void foo(int i) {
        System.out.println(i + ":" + "foo");
    }

}
