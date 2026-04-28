package com.smartcs.dialogue.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcs.dialogue.dto.DialogueResult;
import com.smartcs.dialogue.dto.ProcessMessageRequest;
import com.smartcs.dialogue.service.DialogueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("对话控制器测试")
class DialogueControllerTest {

    @Mock
    private DialogueService dialogueService;

    @InjectMocks
    private DialogueController dialogueController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dialogueController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("处理消息接口测试")
    void testProcessMessageEndpoint() throws Exception {
        ProcessMessageRequest request = new ProcessMessageRequest();
        request.setSessionId(1L);
        request.setUserId(100L);
        request.setContent("我想查询订单");
        request.setMessageType("text");

        DialogueResult result = new DialogueResult();
        result.setSuccess(true);
        result.setStrategy("DIRECT_ANSWER");
        result.setReplyContent("收到您的订单查询请求");

        when(dialogueService.processMessage(any(ProcessMessageRequest.class))).thenReturn(result);

        mockMvc.perform(post("/api/v1/dialogue/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.strategy").value("DIRECT_ANSWER"));
    }
}
