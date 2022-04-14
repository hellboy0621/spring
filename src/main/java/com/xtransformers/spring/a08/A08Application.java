package com.xtransformers.spring.a08;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * http://localhost:8888/test 查看效果
 * 如果 jdk >= 9，运行时添加 --add-opens java.base/java.lang=ALL-UNNAMED
 * 如果反射调用 jdk 中方法，报错 IllegalAccessException
 * 原因：@Lazy 注入的是代理对象，toString 方法，反射调用 Object#toString
 *
 * 1. 更换 jdk 版本
 * 2. 重写 toString 方法
 * 3. 增加启动时VM参数 --add-opens java.base/java.lang=ALL-UNNAMED
 *
 * @author daniel
 * @date 2022-04-13
 */
@SpringBootApplication
public class A08Application {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(A08Application.class, args);
    }
}
