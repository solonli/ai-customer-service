package com.smartcs.dialogue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cs_message")
public class Message {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String messageNo;
    
    private Long sessionId;
    
    private Integer senderType;
    
    private Long senderId;
    
    private String msgType;
    
    private String content;
    
    private String contentSummary;
    
    private String nluResult;
    
    private Long knowledgeId;
    
    private Long replyTemplateId;
    
    private Integer status;
    
    private LocalDateTime createdAt;
}
