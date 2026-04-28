package com.smartcs.operation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "工单DTO")
public class TicketDTO {
    
    @Schema(description = "工单ID")
    private Long id;
    
    @Schema(description = "工单编号")
    private String ticketNo;
    
    @Schema(description = "会话ID")
    private Long sessionId;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "工单标题")
    private String title;
    
    @Schema(description = "工单描述")
    private String description;
    
    @Schema(description = "优先级")
    private Integer priority;
    
    @Schema(description = "状态：0-待处理，1-处理中，2-待确认，3-已解决，4-已关闭，5-已撤销")
    private Integer status;
    
    @Schema(description = "处理人ID")
    private Long handlerId;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
