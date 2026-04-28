package com.smartcs.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cs_kb_entry")
public class KnowledgeEntry {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String entryNo;
    
    private Long categoryId;
    
    private String title;
    
    private String content;
    
    private String contentType;
    
    private String keywords;
    
    private String similarQuestions;
    
    private String intentId;
    
    private String vectorId;
    
    private Integer effectiveCount;
    
    private Integer ineffectiveCount;
    
    private Integer status;
    
    private Integer version;
    
    private Long publisherId;
    
    private LocalDateTime publishedAt;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
