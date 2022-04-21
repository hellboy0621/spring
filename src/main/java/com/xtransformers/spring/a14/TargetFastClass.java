package com.xtransformers.spring.a14;

import org.springframework.cglib.core.Signature;

/**
 * @author daniel
 * @date 2022-04-20
 * @see org.springframework.cglib.reflect.FastClass
 */
public class TargetFastClass {

    static final Signature S0 = new Signature("save", "()V");
    static final Signature S1 = new Signature("save", "(I)V");
    static final Signature S2 = new Signature("save", "(J)V");

    /**
     * 获取目标方法编号
     *
     * save()       0
     * save(int)    1
     * save(long)   2
     *
     * @param signature 方法签名
     * @return 目标方法编号
     */
    public int getIndex(Signature signature) {
        if (S0.equals(signature)) {
            return 0;
        } else if (S1.equals(signature)) {
            return 1;
        } else if (S2.equals(signature)) {
            return 2;
        }
        return -1;
    }

    /**
     * 根据方法编号，正常调用目标对象方法
     *
     * @param index  目标方法编号
     * @param target 目标对象
     * @param args   参数
     * @return
     */
    public Object invoke(int index, Object target, Object[] args) {
        switch (index) {
            case 0:
                ((Target) target).save();
                return null;
            case 1:
                ((Target) target).save(((int) args[0]));
                return null;
            case 2:
                ((Target) target).save(((long) args[0]));
                return null;
            default:
                throw new RuntimeException("No Such Method, index=" + index);
        }
    }

    public static void main(String[] args) {
        TargetFastClass fastClass = new TargetFastClass();
        Signature signature = new Signature("save", "()V");
        int index = fastClass.getIndex(signature);
        System.out.println("index = " + index);
        fastClass.invoke(index, new Target(), new Object[0]);

        signature = new Signature("save", "(I)V");
        index = fastClass.getIndex(signature);
        System.out.println("index = " + index);
        fastClass.invoke(index, new Target(), new Object[]{1});

        signature = new Signature("save", "(J)V");
        index = fastClass.getIndex(signature);
        System.out.println("index = " + index);
        fastClass.invoke(index, new Target(), new Object[]{1L});

        /**
         * index = 0
         * Target.save()
         * index = 1
         * Target.save(int)
         * index = 2
         * Target.save(long)
         */
    }

}
