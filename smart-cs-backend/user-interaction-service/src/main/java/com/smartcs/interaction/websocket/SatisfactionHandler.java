package com.smartcs.interaction.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SatisfactionHandler {

    private final ObjectMapper objectMapper;
    private final WebSocketSessionManager sessionManager;

    @SuppressWarnings("unchecked")
    public void handle(ChannelHandlerContext ctx, Map<String, Object> msgMap) {
        try {
            String msgId = (String) msgMap.get("msg_id");
            Map<String, Object> data = (Map<String, Object>) msgMap.get("data");
            
            String sessionId = (String) data.get("session_id");
            Integer score = ((Number) data.get("score")).intValue();
            String feedback = (String) data.get("feedback");
            
            String userId = sessionManager.getUserId(ctx.channel());
            
            log.info("收到满意度评价: sessionId={}, userId={}, score={}, feedback={}", 
                sessionId, userId, score, feedback);
            
            Map<String, Object> reply = buildSuccessReply(msgId);
            sendMessage(ctx, reply);
            
        } catch (Exception e) {
            log.error("处理满意度评价失败", e);
        }
    }

    private Map<String, Object> buildSuccessReply(String msgId) {
        Map<String, Object> reply = new HashMap<>();
        reply.put("type", "satisfaction_result");
        reply.put("msg_id", msgId);
        reply.put("timestamp", System.currentTimeMillis());
        reply.put("data", Map.of("success", true, "message", "感谢您的评价！"));
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
