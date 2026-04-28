package com.smartcs.interaction.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cs_session")
public class Session {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String sessionNo;
    
    private Long userId;
    
    private String channel;
    
    private Integer sessionType;
    
    private Integer status;
    
    private Long botId;
    
    private Long agentId;
    
    private Long queueId;
    
    private LocalDateTime firstMessageTime;
    
    private LocalDateTime lastMessageTime;
    
    private String transferReason;
    
    private Integer satisfactionScore;
    
    private String satisfactionFeedback;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime endedAt;
}
