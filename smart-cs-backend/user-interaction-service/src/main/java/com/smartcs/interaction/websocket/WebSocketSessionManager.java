package com.smartcs.interaction.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketSessionManager {

    private final Map<String, Channel> channelMap = new ConcurrentHashMap<>();
    private final Map<String, String> channelUserMap = new ConcurrentHashMap<>();
    private final Map<String, String> channelSessionMap = new ConcurrentHashMap<>();

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_CHANNEL_KEY = "ws:user:channel:";
    private static final String CHANNEL_USER_KEY = "ws:channel:user:";

    public WebSocketSessionManager(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addSession(Channel channel, String userId, String sessionId) {
        String channelId = channel.id().asLongText();
        channelMap.put(channelId, channel);
        channelUserMap.put(channelId, userId);
        channelSessionMap.put(channelId, sessionId);
        
        redisTemplate.opsForValue().set(USER_CHANNEL_KEY + userId, channelId);
        redisTemplate.opsForValue().set(CHANNEL_USER_KEY + channelId, userId);
        
        log.info("会话绑定: channelId={}, userId={}, sessionId={}", channelId, userId, sessionId);
    }

    public void removeSession(Channel channel) {
        String channelId = channel.id().asLongText();
        String userId = channelUserMap.remove(channelId);
        
        channelMap.remove(channelId);
        channelSessionMap.remove(channelId);
        
        if (userId != null) {
            redisTemplate.delete(USER_CHANNEL_KEY + userId);
        }
        redisTemplate.delete(CHANNEL_USER_KEY + channelId);
        
        log.info("会话移除: channelId={}, userId={}", channelId, userId);
    }

    public Channel getChannel(String channelId) {
        return channelMap.get(channelId);
    }

    public Channel getChannelByUserId(String userId) {
        String channelId = (String) redisTemplate.opsForValue().get(USER_CHANNEL_KEY + userId);
        return channelId != null ? channelMap.get(channelId) : null;
    }

    public String getUserId(Channel channel) {
        return channelUserMap.get(channel.id().asLongText());
    }

    public String getSessionId(Channel channel) {
        return channelSessionMap.get(channel.id().asLongText());
    }

    public boolean isOnline(String userId) {
        return redisTemplate.hasKey(USER_CHANNEL_KEY + userId);
    }
}
