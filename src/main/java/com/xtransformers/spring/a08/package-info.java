package com.xtransformers.spring.a08;

/**
 * singleton
 * prototype
 * request
 * session
 * application
 *
 * scope 的销毁
 * 单例：Spring 容器关闭时销毁
 * 多例：每次使用时创建，自行实现销毁
 * request 域：请求时创建，请求结束销毁
 * session：会话结束时销毁
 * application：没有实现销毁
 */