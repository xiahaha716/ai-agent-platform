package com.example.aiagentplatform;

import org.mybatis.spring.annotation.MapperScan; // 必须导入这个
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 重点：告诉 Spring 去哪个包里扫你的 Mapper 接口
@MapperScan("com.example.aiagentplatform.mapper")
public class AiAgentPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiAgentPlatformApplication.class, args);
    }
}

