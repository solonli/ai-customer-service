package com.smartcs.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录响应")
public class LoginResponse {
    
    @Schema(description = "访问令牌")
    private String accessToken;
    
    @Schema(description = "刷新令牌")
    private String refreshToken;
    
    @Schema(description = "过期时间(秒)")
    private Long expiresIn;
    
    @Schema(description = "用户信息")
    private UserInfo userInfo;
    
    @Data
    public static class UserInfo {
        private Long userId;
        private String userNo;
        private String nickName;
        private String avatarUrl;
        private Integer userType;
    }
}
