package com.smartcs.dialogue.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcs.dialogue.dto.DialogueResult;
import com.smartcs.dialogue.dto.NLUResult;
import com.smartcs.dialogue.dto.ProcessMessageRequest;
import com.smartcs.dialogue.entity.Message;
import com.smartcs.dialogue.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogueService {

    private final MessageMapper messageMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${dialogue.confidence.threshold:0.85}")
    private Double confidenceThreshold;
    
    @Value("${dialogue.transfer.threshold:0.70}")
    private Double transferThreshold;
    
    private static final String CONTEXT_KEY_PREFIX = "session:context:";
    
    public DialogueResult processMessage(ProcessMessageRequest request) {
        try {
            log.info("开始处理消息: sessionId={}, content={}", request.getSessionId(), request.getContent());
            
            saveUserMessage(request);
            
            DialogueContext context = getContext(request.getSessionId());
            
            NLUResult nluResult = analyzeNLU(request.getContent(), context);
            
            updateContext(request.getSessionId(), nluResult, request.getContent());
            
            String strategy = determineStrategy(nluResult);
            
            DialogueResult result = executeStrategy(request, strategy, nluResult, context);
            
            saveBotMessage(request.getSessionId(), result);
            
            log.info("消息处理完成: strategy={}, sessionId={}", strategy, request.getSessionId());
            
            return result;
            
        } catch (Exception e) {
            log.error("处理消息失败", e);
            return fallback(request);
        }
    }
    
    private NLUResult analyzeNLU(String content, DialogueContext context) {
        NLUResult result = new NLUResult();
        NLUResult.IntentInfo intentInfo = new NLUResult.IntentInfo();
        
        if (content.contains("订单")) {
            intentInfo.setIntentCode("query_order");
            intentInfo.setIntentName("订单查询");
            intentInfo.setConfidence(0.92);
            result.setKeywords(new String[]{"订单", "查询"});
        } else if (content.contains("退款")) {
            intentInfo.setIntentCode("refund_request");
            intentInfo.setIntentName("退款申请");
            intentInfo.setConfidence(0.88);
            result.setKeywords(new String[]{"退款", "申请"});
        } else if (content.contains("客服")) {
            intentInfo.setIntentCode("transfer_human");
            intentInfo.setIntentName("转人工");
            intentInfo.setConfidence(0.95);
            result.setKeywords(new String[]{"客服", "人工"});
        } else {
            intentInfo.setIntentCode("unknown");
            intentInfo.setIntentName("未知");
            intentInfo.setConfidence(0.65);
            result.setKeywords(new String[]{"未知"});
        }
        
        result.setTopIntent(intentInfo);
        result.setEntities(new HashMap<>());
        
        return result;
    }
    
    private String determineStrategy(NLUResult nluResult) {
        Double confidence = nluResult.getTopIntent().getConfidence();
        
        if (confidence >= confidenceThreshold) {
            if ("transfer_human".equals(nluResult.getTopIntent().getIntentCode())) {
                return "TRANSFER_HUMAN";
            }
            return "DIRECT_ANSWER";
        } else if (confidence >= transferThreshold) {
            return "CLARIFY";
        } else {
            return "TRANSFER_HUMAN";
        }
    }
    
    private DialogueResult executeStrategy(ProcessMessageRequest request, String strategy, 
                                           NLUResult nluResult, DialogueContext context) {
        switch (strategy) {
            case "DIRECT_ANSWER":
                return handleDirectAnswer(request, nluResult);
            case "CLARIFY":
                return DialogueResult.clarify("您能再具体描述一下您的问题吗？", nluResult);
            case "TRANSFER_HUMAN":
                return DialogueResult.transfer("正在为您转接人工客服，请稍候...", nluResult);
            default:
                return fallback(request);
        }
    }
    
    private DialogueResult handleDirectAnswer(ProcessMessageRequest request, NLUResult nluResult) {
        String intentCode = nluResult.getTopIntent().getIntentCode();
        
        String reply;
        switch (intentCode) {
            case "query_order":
                reply = "收到您的订单查询请求，请提供订单号或更多信息，我来帮您查询。";
                break;
            case "refund_request":
                reply = "您好！退款流程：1.找到我的订单 2.选择需要退款的商品 3.点击退款按钮 4.填写退款原因并提交。";
                break;
            case "greeting":
                reply = "您好！欢迎咨询，有什么可以帮您的吗？";
                break;
            default:
                reply = "收到您的消息，正在为您处理中...";
        }
        
        return DialogueResult.directAnswer(reply, null, nluResult);
    }
    
    private DialogueResult fallback(ProcessMessageRequest request) {
        DialogueResult result = new DialogueResult();
        result.setSuccess(true);
        result.setStrategy("FALLBACK");
        result.setReplyContent("抱歉，我暂时无法理解您的问题，您可以换个方式描述，或者请求转人工服务。");
        return result;
    }
    
    private void saveUserMessage(ProcessMessageRequest request) {
        Message message = new Message();
        message.setMessageNo(generateMessageNo());
        message.setSessionId(request.getSessionId());
        message.setSenderType(1);
        message.setSenderId(request.getUserId());
        message.setMsgType(request.getMessageType());
        message.setContent(request.getContent());
        message.setStatus(1);
        message.setCreatedAt(LocalDateTime.now());
        
        messageMapper.insert(message);
    }
    
    private void saveBotMessage(Long sessionId, DialogueResult result) {
        Message message = new Message();
        message.setMessageNo(generateMessageNo());
        message.setSessionId(sessionId);
        message.setSenderType(2);
        message.setMsgType("text");
        message.setContent(result.getReplyContent());
        message.setStatus(1);
        message.setCreatedAt(LocalDateTime.now());
        
        if (result.getNluResult() != null) {
            try {
                message.setNluResult(objectMapper.writeValueAsString(result.getNluResult()));
            } catch (Exception e) {
                log.warn("NLU结果序列化失败", e);
            }
        }
        
        if (result.getKnowledgeId() != null) {
            message.setKnowledgeId(result.getKnowledgeId());
        }
        
        messageMapper.insert(message);
    }
    
    private DialogueContext getContext(Long sessionId) {
        String key = CONTEXT_KEY_PREFIX + sessionId;
        DialogueContext context = (DialogueContext) redisTemplate.opsForHash().get(key, "context");
        if (context == null) {
            context = new DialogueContext();
            context.setSessionId(sessionId);
        }
        return context;
    }
    
    private void updateContext(Long sessionId, NLUResult nluResult, String content) {
        String key = CONTEXT_KEY_PREFIX + sessionId;
        DialogueContext context = (DialogueContext) redisTemplate.opsForHash().get(key, "context");
        if (context == null) {
            context = new DialogueContext();
            context.setSessionId(sessionId);
        }
        context.setLastIntent(nluResult.getTopIntent().getIntentCode());
        context.setLastContent(content);
        context.setTurnCount(context.getTurnCount() + 1);
        
        redisTemplate.opsForHash().put(key, "context", context);
    }
    
    private String generateMessageNo() {
        return "M" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
    
    @lombok.Data
    public static class DialogueContext {
        private Long sessionId;
        private String lastIntent;
        private String lastContent;
        private Integer turnCount = 0;
    }
}
