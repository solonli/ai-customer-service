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
public class MessageDispatcher {

    private final ObjectMapper objectMapper;
    private final WebSocketSessionManager sessionManager;
    private final ChatMessageHandler chatMessageHandler;
    private final TransferHandler transferHandler;
    private final SatisfactionHandler satisfactionHandler;

    public void dispatch(ChannelHandlerContext ctx, String message) {
        try {
            Map<String, Object> msgMap = objectMapper.readValue(message, Map.class);
            String type = (String) msgMap.get("type");
            
            log.debug("分发消息: type={}", type);
            
            switch (type) {
                case "chat_message":
                    chatMessageHandler.handle(ctx, msgMap);
                    break;
                case "transfer_request":
                    transferHandler.handle(ctx, msgMap);
                    break;
                case "satisfaction":
                    satisfactionHandler.handle(ctx, msgMap);
                    break;
                case "ping":
                    handlePing(ctx);
                    break;
                default:
                    log.warn("未知消息类型: {}", type);
                    sendError(ctx, "未知消息类型: " + type);
            }
        } catch (Exception e) {
            log.error("消息解析失败", e);
            sendError(ctx, "消息格式错误");
        }
    }

    private void handlePing(ChannelHandlerContext ctx) {
        Map<String, Object> pong = new HashMap<>();
        pong.put("type", "pong");
        pong.put("timestamp", System.currentTimeMillis());
        sendMessage(ctx, pong);
    }

    private void sendError(ChannelHandlerContext ctx, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("type", "error");
        error.put("message", message);
        sendMessage(ctx, error);
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
