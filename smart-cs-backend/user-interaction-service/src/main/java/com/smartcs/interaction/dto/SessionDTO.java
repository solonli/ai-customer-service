package com.smartcs.interaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "会话信息")
public class SessionDTO {
    
    @Schema(description = "会话ID")
    private Long id;
    
    @Schema(description = "会话编号")
    private String sessionNo;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "接入渠道")
    private String channel;
    
    @Schema(description = "会话类型: 1-机器人 2-人工 3-机器人转人工")
    private Integer sessionType;
    
    @Schema(description = "状态: 0-排队中 1-进行中 2-已结束 3-已超时")
    private Integer status;
    
    @Schema(description = "关联客服ID")
    private Long agentId;
    
    @Schema(description = "客服名称")
    private String agentName;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    @Schema(description = "最后消息时间")
    private LocalDateTime lastMessageTime;
}
