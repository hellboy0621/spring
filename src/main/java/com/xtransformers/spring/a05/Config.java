package com.xtransformers.spring.a05;

import com.alibaba.druid.pool.DruidDataSource;
import com.xtransformers.spring.a05.component.Bean2;
import com.xtransformers.spring.a05.mapper.Mapper1;
import com.xtransformers.spring.a05.mapper.Mapper2;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author daniel
 * @date 2022-04-05
 */
@Configuration
@ComponentScan("com.xtransformers.spring.a05.component")
public class Config {

    @Bean
    public Bean1 bean1() {
        return new Bean1();
    }

    public Bean2 bean2() {
        return new Bean2();
    }

    @Bean(initMethod = "init")
    public DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean;
    }

    // @Bean
    // public MapperFactoryBean<Mapper1> mapper1(SqlSessionFactory sqlSessionFactory) {
    //     MapperFactoryBean<Mapper1> mapperFactoryBean = new MapperFactoryBean<>(Mapper1.class);
    //     mapperFactoryBean.setSqlSessionFactory(sqlSessionFactory);
    //     return mapperFactoryBean;
    // }
    //
    // @Bean
    // public MapperFactoryBean<Mapper2> mapper2(SqlSessionFactory sqlSessionFactory) {
    //     MapperFactoryBean<Mapper2> mapperFactoryBean = new MapperFactoryBean<>(Mapper2.class);
    //     mapperFactoryBean.setSqlSessionFactory(sqlSessionFactory);
    //     return mapperFactoryBean;
    // }

}
