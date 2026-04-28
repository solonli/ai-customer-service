package com.smartcs.dialogue.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcs.dialogue.dto.DialogueResult;
import com.smartcs.dialogue.dto.ProcessMessageRequest;
import com.smartcs.dialogue.entity.Message;
import com.smartcs.dialogue.mapper.MessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("对话服务测试")
class DialogueServiceTest {

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DialogueService dialogueService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(dialogueService, "confidenceThreshold", 0.85);
        ReflectionTestUtils.setField(dialogueService, "transferThreshold", 0.70);
    }

    @Test
    @DisplayName("处理订单查询消息")
    void testProcessOrderQueryMessage() {
        ProcessMessageRequest request = new ProcessMessageRequest();
        request.setSessionId(1L);
        request.setUserId(100L);
        request.setContent("我想查询订单");
        request.setMessageType("text");

        when(messageMapper.insert(any(Message.class))).thenReturn(1);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(null);

        DialogueResult result = dialogueService.processMessage(request);

        assertNotNull(result, "对话结果不应为空");
        assertTrue(result.getSuccess(), "处理应成功");
        assertNotNull(result.getReplyContent(), "回复内容不应为空");
        assertNotNull(result.getStrategy(), "策略不应为空");
    }

    @Test
    @DisplayName("处理退款申请消息")
    void testProcessRefundMessage() {
        ProcessMessageRequest request = new ProcessMessageRequest();
        request.setSessionId(1L);
        request.setUserId(100L);
        request.setContent("我要退款");
        request.setMessageType("text");

        when(messageMapper.insert(any(Message.class))).thenReturn(1);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(null);

        DialogueResult result = dialogueService.processMessage(request);

        assertNotNull(result, "对话结果不应为空");
        assertTrue(result.getSuccess(), "处理应成功");
        assertNotNull(result.getNluResult(), "NLU结果不应为空");
    }

    @Test
    @DisplayName("处理转人工消息")
    void testProcessTransferHumanMessage() {
        ProcessMessageRequest request = new ProcessMessageRequest();
        request.setSessionId(1L);
        request.setUserId(100L);
        request.setContent("转人工客服");
        request.setMessageType("text");

        when(messageMapper.insert(any(Message.class))).thenReturn(1);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(null);

        DialogueResult result = dialogueService.processMessage(request);

        assertNotNull(result, "对话结果不应为空");
        assertEquals("TRANSFER_HUMAN", result.getStrategy(), "策略应为转人工");
        assertTrue(result.getNeedTransfer(), "应需要转人工");
    }

    @Test
    @DisplayName("处理问候消息")
    void testProcessGreetingMessage() {
        ProcessMessageRequest request = new ProcessMessageRequest();
        request.setSessionId(1L);
        request.setUserId(100L);
        request.setContent("你好");
        request.setMessageType("text");

        when(messageMapper.insert(any(Message.class))).thenReturn(1);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(null);

        DialogueResult result = dialogueService.processMessage(request);

        assertNotNull(result, "对话结果不应为空");
        assertTrue(result.getSuccess(), "处理应成功");
    }

    @Test
    @DisplayName("处理未知消息-回退策略")
    void testProcessUnknownMessage() {
        ProcessMessageRequest request = new ProcessMessageRequest();
        request.setSessionId(1L);
        request.setUserId(100L);
        request.setContent("这是一条随机消息xyz123");
        request.setMessageType("text");

        when(messageMapper.insert(any(Message.class))).thenReturn(1);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get(anyString(), anyString())).thenReturn(null);

        DialogueResult result = dialogueService.processMessage(request);

        assertNotNull(result, "对话结果不应为空");
        assertTrue(result.getSuccess(), "处理应成功");
        assertNotNull(result.getReplyContent(), "应有回复内容");
    }
}
