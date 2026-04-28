package com.smartcs.dialogue.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "处理用户消息请求")
public class ProcessMessageRequest {
    
    @Schema(description = "会话ID", required = true)
    private Long sessionId;
    
    @Schema(description = "用户ID", required = true)
    private Long userId;
    
    @Schema(description = "消息内容", required = true)
    private String content;
    
    @Schema(description = "消息类型", defaultValue = "text")
    private String messageType = "text";
}
