package com.smartcs.interaction.service;

import com.smartcs.interaction.dto.SessionCreateRequest;
import com.smartcs.interaction.dto.SessionDTO;
import com.smartcs.interaction.entity.Session;
import com.smartcs.interaction.mapper.SessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionMapper sessionMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String SESSION_KEY_PREFIX = "session:info:";
    private static final String SESSION_CONTEXT_PREFIX = "session:context:";

    public SessionDTO createSession(SessionCreateRequest request) {
        Session session = new Session();
        session.setSessionNo(generateSessionNo());
        session.setUserId(request.getUserId());
        session.setChannel(request.getChannel());
        session.setSessionType(1);
        session.setStatus(1);
        session.setCreatedAt(LocalDateTime.now());
        
        sessionMapper.insert(session);
        
        cacheSession(session);
        
        log.info("会话创建成功: sessionId={}, userId={}", session.getId(), session.getUserId());
        
        return convertToDTO(session);
    }

    public SessionDTO getSessionById(Long id) {
        Session session = (Session) redisTemplate.opsForHash().get(SESSION_KEY_PREFIX + id, "info");
        if (session == null) {
            session = sessionMapper.selectById(id);
            if (session != null) {
                cacheSession(session);
            }
        }
        return session != null ? convertToDTO(session) : null;
    }

    public void closeSession(Long id) {
        Session session = sessionMapper.selectById(id);
        if (session != null && session.getStatus() != 2) {
            session.setStatus(2);
            session.setEndedAt(LocalDateTime.now());
            sessionMapper.updateById(session);
            
            redisTemplate.delete(SESSION_KEY_PREFIX + id);
            redisTemplate.delete(SESSION_CONTEXT_PREFIX + id);
            
            log.info("会话关闭成功: sessionId={}", id);
        }
    }

    public void transferToAgent(Long id, String reason) {
        Session session = sessionMapper.selectById(id);
        if (session != null && session.getStatus() == 1) {
            session.setSessionType(3);
            session.setTransferReason(reason);
            sessionMapper.updateById(session);
            
            log.info("会话转人工成功: sessionId={}, reason={}", id, reason);
        }
    }

    private String generateSessionNo() {
        return "S" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private void cacheSession(Session session) {
        redisTemplate.opsForHash().put(SESSION_KEY_PREFIX + session.getId(), "info", session);
    }

    private SessionDTO convertToDTO(Session session) {
        SessionDTO dto = new SessionDTO();
        dto.setId(session.getId());
        dto.setSessionNo(session.getSessionNo());
        dto.setUserId(session.getUserId());
        dto.setChannel(session.getChannel());
        dto.setSessionType(session.getSessionType());
        dto.setStatus(session.getStatus());
        dto.setAgentId(session.getAgentId());
        dto.setCreatedAt(session.getCreatedAt());
        dto.setLastMessageTime(session.getLastMessageTime());
        return dto;
    }
}
