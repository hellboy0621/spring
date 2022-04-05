package com.xtransformers.spring.a04;

import org.springframework.context.support.GenericApplicationContext;

/**
 * @author daniel
 * @date 2022-04-05
 */
public class A04Application {

    public static void main(String[] args) {
        // GenericApplicationContext 是一个【干净】的容器，排除其他干扰
        GenericApplicationContext context = new GenericApplicationContext();

        // 用原始方法注册三个 bean
        context.registerBean("bean1", Bean1.class);
        context.registerBean("bean2", Bean2.class);
        context.registerBean("bean3", Bean3.class);

        // 初始化容器
        context.refresh();

        // 销毁容器
        context.close();
    }
}
