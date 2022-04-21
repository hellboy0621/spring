package com.xtransformers.spring.a14;

import java.util.Objects;
import org.springframework.cglib.core.Signature;

/**
 * @author daniel
 * @date 2022-04-20
 */
public class ProxyFastClass {

    static final Signature S3 = new Signature("saveSuper", "()V");
    static final Signature S4 = new Signature("saveSuper", "(I)V");
    static final Signature S5 = new Signature("saveSuper", "(J)V");

    /**
     * 获取代理方法编号
     * saveSuper()      3
     * saveSuper(int)   4
     * saveSuper(long)  5
     *
     * @param signature 方法签名
     * @return 代理方法编号
     */
    public int getIndex(Signature signature) {
        if (Objects.equals(S3, signature)) {
            return 3;
        } else if (Objects.equals(S4, signature)) {
            return 4;
        } else if (Objects.equals(S5, signature)) {
            return 5;
        }
        return -1;
    }

    /**
     * 根据代理方法编号，正常调用代理对象方法
     *
     * @param index 代理对象方法编号
     * @param proxy 代理对象
     * @param args  方法参数
     * @return
     */
    public Object invoke(int index, Object proxy, Object[] args) {
        switch (index) {
            case 3:
                ((Proxy) proxy).saveSuper();
                return null;
            case 4:
                ((Proxy) proxy).saveSuper((int) args[0]);
                return null;
            case 5:
                ((Proxy) proxy).saveSuper((long) args[0]);
                return null;
            default:
                throw new RuntimeException("No Such Method, index=" + index);
        }
    }

    public static void main(String[] args) {
        ProxyFastClass fastClass = new ProxyFastClass();
        Signature signature = new Signature("saveSuper", "()V");
        int index = fastClass.getIndex(signature);
        System.out.println("index = " + index);
        fastClass.invoke(index, new Proxy(), new Object[0]);

        signature = new Signature("saveSuper", "(I)V");
        index = fastClass.getIndex(signature);
        System.out.println("index = " + index);
        fastClass.invoke(index, new Proxy(), new Object[]{1});

        signature = new Signature("saveSuper", "(J)V");
        index = fastClass.getIndex(signature);
        System.out.println("index = " + index);
        fastClass.invoke(index, new Proxy(), new Object[]{2L});

        /**
         * index = 3
         * Target.save()
         * index = 4
         * Target.save(int)
         * index = 5
         * Target.save(long)
         */
    }

}
