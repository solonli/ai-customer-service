package com.smartcs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("集成测试基类")
class BaseIntegrationTest {

    @Test
    @DisplayName("应用上下文加载测试")
    void contextLoads() {
    }
}
