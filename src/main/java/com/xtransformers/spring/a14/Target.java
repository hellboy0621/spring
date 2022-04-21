package com.xtransformers.spring.a14;

/**
 * 目标类
 */
public class Target {

    public void save() {
        System.out.println("Target.save()");
    }

    public void save(int i) {
        System.out.println("Target.save(int)");
    }

    public void save(long l) {
        System.out.println("Target.save(long)");
    }

}
