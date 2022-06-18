package com.xtransformers.spring.a21;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public class A21 {

    static class Controller {
        public void test(
                @RequestParam("name1") String name1,
                String name2,
                @RequestParam("age") int age,
                @RequestParam(name = "home", defaultValue = "${JAVA_HOME}") String home1,
                @RequestParam("file") MultipartFile file,
                @PathVariable("id") int id,
                @RequestHeader("Content-Type") String header,
                @CookieValue("token") String token,
                @Value("${JAVA_HOME}") String home2,
                HttpServletRequest request,
                @ModelAttribute User user1,
                User user2,
                @RequestBody User user3
        ) {
        }
    }

    static class User {

    }

    public static void main(String[] args) {

    }
}
