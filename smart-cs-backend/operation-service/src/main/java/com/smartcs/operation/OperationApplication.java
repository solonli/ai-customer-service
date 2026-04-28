package com.smartcs.operation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.smartcs.operation.mapper")
public class OperationApplication {
    public static void main(String[] args) {
        SpringApplication.run(OperationApplication.class, args);
    }
}
