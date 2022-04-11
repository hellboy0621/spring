package com.xtransformers.spring.a06;

/**
 * Aware 接口
 * 1. Aware 接口提供了一种【内置】的注入手段，可以注入 BeanFactory ApplicationContext
 * 2. InitializingBean 接口提供了一种【内置】的初始化手段
 * 3. 内置的注入和初始化不受扩展功能的影响，总会被执行，因此 Spring 框架内部的类常用它们
 * 4. 实战：@Autowired 失效分析
 */