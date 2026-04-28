package com.smartcs.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcs.auth.dto.LoginRequest;
import com.smartcs.auth.dto.LoginResponse;
import com.smartcs.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("认证控制器测试")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("登录接口测试")
    void testLoginEndpoint() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("13812345678");
        request.setPassword("password123");

        LoginResponse response = new LoginResponse();
        response.setAccessToken("test_access_token");
        response.setRefreshToken("test_refresh_token");
        response.setExpiresIn(7200L);

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("test_access_token"));
    }

    @Test
    @DisplayName("登出接口测试")
    void testLogoutEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                .header("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("刷新Token接口测试")
    void testRefreshTokenEndpoint() throws Exception {
        LoginResponse response = new LoginResponse();
        response.setAccessToken("new_access_token");
        response.setRefreshToken("new_refresh_token");
        response.setExpiresIn(7200L);

        when(authService.refreshToken("test_refresh_token")).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/refresh")
                .param("refreshToken", "test_refresh_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("new_access_token"));
    }
}
