package com.code.usermanagerservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

//@ComponentScan(basePackages = {"com.code"})
@MapperScan("com.code.usermanagerservice.mapper")
@SpringBootApplication
@EnableFeignClients
public class UserManagerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserManagerServiceApplication.class, args);
    }

}
