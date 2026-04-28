package com.smartcs.dialogue;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.smartcs.dialogue.mapper")
public class DialogueApplication {
    public static void main(String[] args) {
        SpringApplication.run(DialogueApplication.class, args);
    }
}
