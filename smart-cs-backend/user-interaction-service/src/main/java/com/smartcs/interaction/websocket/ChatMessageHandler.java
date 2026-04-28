package com.smartcs.interaction.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageHandler {

    private final ObjectMapper objectMapper;
    private final WebSocketSessionManager sessionManager;

    @SuppressWarnings("unchecked")
    public void handle(ChannelHandlerContext ctx, Map<String, Object> msgMap) {
        try {
            String msgId = (String) msgMap.get("msg_id");
            Long timestamp = ((Number) msgMap.get("timestamp")).longValue();
            Map<String, Object> data = (Map<String, Object>) msgMap.get("data");
            
            String messageType = (String) data.get("message_type");
            String content = (String) data.get("content");
            
            String sessionId = sessionManager.getSessionId(ctx.channel());
            String userId = sessionManager.getUserId(ctx.channel());
            
            log.info("收到聊天消息: sessionId={}, userId={}, type={}, content={}", 
                sessionId, userId, messageType, content);
            
            Map<String, Object> reply = buildReply(msgId, sessionId, content);
            sendMessage(ctx, reply);
            
        } catch (Exception e) {
            log.error("处理聊天消息失败", e);
        }
    }

    private Map<String, Object> buildReply(String msgId, String sessionId, String userContent) {
        Map<String, Object> reply = new HashMap<>();
        reply.put("type", "chat_reply");
        reply.put("msg_id", msgId);
        reply.put("timestamp", System.currentTimeMillis());
        
        Map<String, Object> data = new HashMap<>();
        data.put("message_id", "M" + System.currentTimeMillis());
        data.put("session_id", sessionId);
        data.put("sender_type", "bot");
        data.put("message_type", "text");
        data.put("content", "收到您的消息：" + userContent + "，正在为您处理中...");
        
        Map<String, Object> nluResult = new HashMap<>();
        nluResult.put("intent", "unknown");
        nluResult.put("confidence", 0.95);
        data.put("nlu_result", nluResult);
        
        reply.put("data", data);
        return reply;
    }

    private void sendMessage(ChannelHandlerContext ctx, Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            ctx.writeAndFlush(new io.netty.handler.codec.http.websocketx.TextWebSocketFrame(json));
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }
    }
}
