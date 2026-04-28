package com.smartcs.nlu.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcs.nlu.model.NLURequest;
import com.smartcs.nlu.model.NLUResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("NLU服务集成测试")
class NLUIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("完整NLU分析流程测试")
    void testFullNLUAnalysisFlow() throws Exception {
        NLURequest request = new NLURequest();
        request.setText("你好，我想查询订单JD20240115001，我的手机号是13812345678");

        MvcResult result = mockMvc.perform(post("/api/v1/nlu/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.topIntent").exists())
                .andExpect(jsonPath("$.data.entities").isArray())
                .andExpect(jsonPath("$.data.keywords").isArray())
                .andExpect(jsonPath("$.data.sentiment").exists())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        assertNotNull(responseJson, "响应不应为空");
    }

    @Test
    @DisplayName("意图识别接口测试")
    void testIntentRecognitionEndpoint() throws Exception {
        NLURequest request = new NLURequest();
        request.setText("转人工客服");

        mockMvc.perform(post("/api/v1/nlu/intent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.intents").isArray());
    }

    @Test
    @DisplayName("实体提取接口测试")
    void testEntityExtractionEndpoint() throws Exception {
        NLURequest request = new NLURequest();
        request.setText("订单号是JD20240115001，金额99.9元");

        mockMvc.perform(post("/api/v1/nlu/entities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.entities").isArray());
    }

    @Test
    @DisplayName("多种意图测试")
    void testMultipleIntents() throws Exception {
        String[] testCases = {
            "你好",
            "我想查订单",
            "我要退款",
            "转人工",
            "谢谢"
        };

        for (String text : testCases) {
            NLURequest request = new NLURequest();
            request.setText(text);

            mockMvc.perform(post("/api/v1/nlu/analyze")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }
}
