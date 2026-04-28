package com.smartcs.operation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cs_ticket")
public class Ticket {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String ticketNo;
    
    private Long sessionId;
    
    private Long userId;
    
    private String title;
    
    private String description;
    
    private Long categoryId;
    
    private Integer priority;
    
    private Integer status;
    
    private String source;
    
    private Long handlerId;
    
    private Long handlerGroupId;
    
    private LocalDateTime slaDeadline;
    
    private LocalDateTime resolvedAt;
    
    private LocalDateTime closedAt;
    
    private Integer satisfactionScore;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
