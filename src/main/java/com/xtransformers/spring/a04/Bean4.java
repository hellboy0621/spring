package com.xtransformers.spring.a04;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 会自动找如下键值对
 * java.home=
 * java.version=
 *
 * @author daniel
 * @date 2022-04-05
 */
@ConfigurationProperties(prefix = "java")
public class Bean4 {
    private String home;
    private String version;

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Bean4{" +
                "home='" + home + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
