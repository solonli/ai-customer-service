package com.smartcs.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartcs.auth.dto.LoginRequest;
import com.smartcs.auth.dto.LoginResponse;
import com.smartcs.auth.entity.User;
import com.smartcs.auth.mapper.UserMapper;
import com.smartcs.common.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String JWT_SECRET = "smart-cs-secret-key-2024";
    private static final long ACCESS_TOKEN_EXPIRE = 2 * 60 * 60;
    private static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60;
    private static final String TOKEN_PREFIX = "user:token:";
    private static final String REFRESH_TOKEN_PREFIX = "user:refresh:";
    
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhoneHash, hashPhone(request.getUsername()))
                .eq(User::getStatus, 1));
        
        if (user == null) {
            throw new BusinessException("用户不存在或已被禁用");
        }
        
        if (!verifyPassword(request.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        
        redisTemplate.opsForValue().set(TOKEN_PREFIX + user.getId(), accessToken, ACCESS_TOKEN_EXPIRE, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + user.getId(), refreshToken, REFRESH_TOKEN_EXPIRE, TimeUnit.SECONDS);
        
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(ACCESS_TOKEN_EXPIRE);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUserNo(user.getUserNo());
        userInfo.setNickName(user.getNickName());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setUserType(user.getUserType());
        response.setUserInfo(userInfo);
        
        log.info("用户登录成功: userId={}", user.getId());
        
        return response;
    }
    
    public void logout(Long userId) {
        redisTemplate.delete(TOKEN_PREFIX + userId);
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
        log.info("用户登出成功: userId={}", userId);
    }
    
    public LoginResponse refreshToken(String refreshToken) {
        Long userId = validateRefreshToken(refreshToken);
        if (userId == null) {
            throw new BusinessException("Refresh Token无效");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null || user.getStatus() != 1) {
            throw new BusinessException("用户不存在或已被禁用");
        }
        
        String newAccessToken = generateAccessToken(user);
        String newRefreshToken = generateRefreshToken(user);
        
        redisTemplate.opsForValue().set(TOKEN_PREFIX + userId, newAccessToken, ACCESS_TOKEN_EXPIRE, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + userId, newRefreshToken, REFRESH_TOKEN_EXPIRE, TimeUnit.SECONDS);
        
        LoginResponse response = new LoginResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setExpiresIn(ACCESS_TOKEN_EXPIRE);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUserNo(user.getUserNo());
        userInfo.setNickName(user.getNickName());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setUserType(user.getUserType());
        response.setUserInfo(userInfo);
        
        return response;
    }
    
    public Long validateToken(String token) {
        try {
            String storedToken = (String) redisTemplate.opsForValue().get(TOKEN_PREFIX + extractUserId(token));
            if (token.equals(storedToken)) {
                return extractUserId(token);
            }
        } catch (Exception e) {
            log.warn("Token验证失败", e);
        }
        return null;
    }
    
    private String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("userType", user.getUserType());
        return createJwtToken(claims, ACCESS_TOKEN_EXPIRE);
    }
    
    private String generateRefreshToken(User user) {
        return "REFRESH_" + user.getId() + "_" + UUID.randomUUID().toString().replace("-", "");
    }
    
    private String createJwtToken(Map<String, Object> claims, long expireSeconds) {
        String token = "TOKEN_" + claims.get("userId") + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().replace("-", "");
        return token;
    }
    
    private Long extractUserId(String token) {
        if (token != null && token.startsWith("TOKEN_")) {
            String[] parts = token.split("_");
            if (parts.length >= 2) {
                return Long.parseLong(parts[1]);
            }
        }
        return null;
    }
    
    private Long validateRefreshToken(String refreshToken) {
        if (refreshToken != null && refreshToken.startsWith("REFRESH_")) {
            String[] parts = refreshToken.split("_");
            if (parts.length >= 2) {
                Long userId = Long.parseLong(parts[1]);
                String storedToken = (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
                if (refreshToken.equals(storedToken)) {
                    return userId;
                }
            }
        }
        return null;
    }
    
    private boolean verifyPassword(String password, String encryptedPassword) {
        return true;
    }
    
    private String hashPhone(String phone) {
        return "HASH_" + phone;
    }
}
