package com.xtransformers.spring.a03;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author daniel
 * @date 2022-04-05
 */
@SpringBootApplication
public class A03Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(A03Application.class, args);
        context.close();
    }
    /*
    2022-04-05 14:56:37.795  INFO 67080 --- [           main] c.x.spring.a03.A03Application            : Starting A03Application using Java 17.0.1 on DanieldeMacBook-Pro.local with PID 67080 (/Users/daniel/Documents/code/spring/target/classes started by daniel in /Users/daniel/Documents/code/spring)
    2022-04-05 14:56:37.798  INFO 67080 --- [           main] c.x.spring.a03.A03Application            : No active profile set, falling back to 1 default profile: "default"
    2022-04-05 14:56:39.864  INFO 67080 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8888 (http)
    2022-04-05 14:56:39.878  INFO 67080 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
    2022-04-05 14:56:39.878  INFO 67080 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.60]
    2022-04-05 14:56:39.999  INFO 67080 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
    2022-04-05 14:56:40.000  INFO 67080 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 2139 ms
    2022-04-05 14:56:40.108  INFO 67080 --- [           main] c.x.spring.a03.BeanPostProcessorTest     : postProcessBeforeInstantiation <<< 实例化之前执行，这里返回的对象如果不为 null 就会替换掉原本的 bean
    2022-04-05 14:56:40.111  INFO 67080 --- [           main] c.x.spring.a03.LifeCycleBean             : Constructor
    2022-04-05 14:56:40.125  INFO 67080 --- [           main] c.x.spring.a03.BeanPostProcessorTest     : postProcessAfterInstantiation <<< 实例化之后执行，这里返回 false 会跳过依赖注入阶段
    2022-04-05 14:56:40.126  INFO 67080 --- [           main] c.x.spring.a03.BeanPostProcessorTest     : postProcessProperties 依赖注入阶段执行，如 @Autowired @Value @Resource
    2022-04-05 14:56:40.169  INFO 67080 --- [           main] c.x.spring.a03.LifeCycleBean             : Dependency injection name:/Users/daniel/.jenv/versions/1.8.0.301
    2022-04-05 14:56:40.171  INFO 67080 --- [           main] c.x.spring.a03.BeanPostProcessorTest     : postProcessBeforeInitialization <<< 初始化之前执行，这里返回的对象不为 null 会替换掉原本的 bean，如 @PostConstruct @ConfigurationProperties
    2022-04-05 14:56:40.172  INFO 67080 --- [           main] c.x.spring.a03.LifeCycleBean             : init @PostConstruct
    2022-04-05 14:56:40.172  INFO 67080 --- [           main] c.x.spring.a03.BeanPostProcessorTest     : postProcessAfterInitialization <<< 初始化之后执行，这里返回的对象会替换掉原本的 bean，如 代理增强
    2022-04-05 14:56:40.906  INFO 67080 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8888 (http) with context path ''
    2022-04-05 14:56:40.928  INFO 67080 --- [           main] c.x.spring.a03.A03Application            : Started A03Application in 3.653 seconds (JVM running for 4.692)
    2022-04-05 14:56:40.980  INFO 67080 --- [           main] c.x.spring.a03.BeanPostProcessorTest     : postProcessBeforeDestruction <<< 销毁之前执行，如 @PreDestroy
    2022-04-05 14:56:40.981  INFO 67080 --- [           main] c.x.spring.a03.LifeCycleBean             : destroy @PreDestroy
     */
}
