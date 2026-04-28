package com.smartcs.auth.controller;

import com.smartcs.common.core.result.Result;
import com.smartcs.auth.dto.LoginRequest;
import com.smartcs.auth.dto.LoginResponse;
import com.smartcs.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证", description = "登录、登出、Token刷新等接口")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "登录", description = "用户登录获取Token")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }

    @Operation(summary = "登出", description = "用户登出，清理Token")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("userId") Long userId) {
        authService.logout(userId);
        return Result.success();
    }

    @Operation(summary = "刷新Token", description = "使用Refresh Token获取新的Access Token")
    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        LoginResponse response = authService.refreshToken(refreshToken);
        return Result.success(response);
    }
}
