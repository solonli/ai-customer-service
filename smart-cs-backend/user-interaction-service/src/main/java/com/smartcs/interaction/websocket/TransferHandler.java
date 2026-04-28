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
public class TransferHandler {

    private final ObjectMapper objectMapper;
    private final WebSocketSessionManager sessionManager;

    @SuppressWarnings("unchecked")
    public void handle(ChannelHandlerContext ctx, Map<String, Object> msgMap) {
        try {
            String msgId = (String) msgMap.get("msg_id");
            Map<String, Object> data = (Map<String, Object>) msgMap.get("data");
            String reason = (String) data.get("reason");
            
            String sessionId = sessionManager.getSessionId(ctx.channel());
            String userId = sessionManager.getUserId(ctx.channel());
            
            log.info("收到转人工请求: sessionId={}, userId={}, reason={}", sessionId, userId, reason);
            
            Map<String, Object> reply = buildTransferReply(msgId);
            sendMessage(ctx, reply);
            
        } catch (Exception e) {
            log.error("处理转人工请求失败", e);
        }
    }

    private Map<String, Object> buildTransferReply(String msgId) {
        Map<String, Object> reply = new HashMap<>();
        reply.put("type", "session_transferred");
        reply.put("msg_id", msgId);
        reply.put("timestamp", System.currentTimeMillis());
        
        Map<String, Object> data = new HashMap<>();
        data.put("agent_name", "客服小王");
        data.put("queue_position", 3);
        data.put("estimated_wait_time", 60);
        
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
