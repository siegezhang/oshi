package com.allin.oshi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author xiaosheng.zhang
 * @date 20200713
 */
@SpringBootApplication
@ComponentScan({"com.allin.oshi"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
