package com.smartcs.auth.service;

import com.smartcs.auth.dto.LoginRequest;
import com.smartcs.auth.dto.LoginResponse;
import com.smartcs.auth.entity.User;
import com.smartcs.auth.mapper.UserMapper;
import com.smartcs.common.core.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("认证服务测试")
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserNo("U001");
        testUser.setNickName("测试用户");
        testUser.setPhoneHash("HASH_13812345678");
        testUser.setPassword("encrypted_password");
        testUser.setUserType(1);
        testUser.setStatus(1);
        testUser.setAvatarUrl("http://example.com/avatar.png");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("登录成功测试")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setUsername("13812345678");
        request.setPassword("password123");

        when(userMapper.selectOne(any())).thenReturn(testUser);

        LoginResponse response = authService.login(request);

        assertNotNull(response, "登录响应不应为空");
        assertNotNull(response.getAccessToken(), "访问令牌不应为空");
        assertNotNull(response.getRefreshToken(), "刷新令牌不应为空");
        assertNotNull(response.getUserInfo(), "用户信息不应为空");
        assertEquals(testUser.getId(), response.getUserInfo().getUserId(), "用户ID应匹配");

        verify(valueOperations, times(2)).set(anyString(), any(), anyLong(), any());
    }

    @Test
    @DisplayName("登录失败-用户不存在")
    void testLoginFailUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("13800000000");
        request.setPassword("password123");

        when(userMapper.selectOne(any())).thenReturn(null);

        assertThrows(BusinessException.class, () -> {
            authService.login(request);
        }, "用户不存在应抛出异常");
    }

    @Test
    @DisplayName("登录失败-用户被禁用")
    void testLoginFailUserDisabled() {
        LoginRequest request = new LoginRequest();
        request.setUsername("13812345678");
        request.setPassword("password123");

        testUser.setStatus(0);
        when(userMapper.selectOne(any())).thenReturn(null);

        assertThrows(BusinessException.class, () -> {
            authService.login(request);
        }, "用户被禁用应抛出异常");
    }

    @Test
    @DisplayName("登出成功测试")
    void testLogoutSuccess() {
        Long userId = 1L;

        authService.logout(userId);

        verify(redisTemplate, times(2)).delete(anyString());
    }

    @Test
    @DisplayName("刷新Token成功测试")
    void testRefreshTokenSuccess() {
        String refreshToken = "REFRESH_1_abc123";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("user:refresh:1")).thenReturn(refreshToken);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        LoginResponse response = authService.refreshToken(refreshToken);

        assertNotNull(response, "刷新响应不应为空");
        assertNotNull(response.getAccessToken(), "新的访问令牌不应为空");
    }

    @Test
    @DisplayName("刷新Token失败-无效Token")
    void testRefreshTokenFailInvalidToken() {
        String refreshToken = "INVALID_TOKEN";

        assertThrows(BusinessException.class, () -> {
            authService.refreshToken(refreshToken);
        }, "无效Token应抛出异常");
    }
}
