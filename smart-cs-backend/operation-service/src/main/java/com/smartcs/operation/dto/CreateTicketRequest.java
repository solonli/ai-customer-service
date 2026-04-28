package com.smartcs.operation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建工单请求")
public class CreateTicketRequest {
    
    @Schema(description = "会话ID")
    private Long sessionId;
    
    @Schema(description = "用户ID", required = true)
    private Long userId;
    
    @Schema(description = "工单标题", required = true)
    private String title;
    
    @Schema(description = "工单描述")
    private String description;
    
    @Schema(description = "分类ID")
    private Long categoryId;
    
    @Schema(description = "优先级：1-低，2-中，3-高，4-紧急", defaultValue = "2")
    private Integer priority = 2;
    
    @Schema(description = "来源", defaultValue = "chat")
    private String source = "chat";
}
