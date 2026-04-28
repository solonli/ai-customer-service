package com.smartcs.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录请求")
public class LoginRequest {
    
    @Schema(description = "用户名/手机号", required = true)
    private String username;
    
    @Schema(description = "密码", required = true)
    private String password;
    
    @Schema(description = "登录验证码")
    private String captcha;
}
