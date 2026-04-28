package com.smartcs.interaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建会话请求")
public class SessionCreateRequest {
    
    @Schema(description = "用户ID", required = true)
    private Long userId;
    
    @Schema(description = "接入渠道", allowableValues = {"web", "wechat", "app"})
    private String channel = "web";
    
    @Schema(description = "渠道用户标识")
    private String channelUserId;
    
    @Schema(description = "设备信息")
    private String deviceInfo;
    
    @Schema(description = "来源页面")
    private String sourcePage;
}
