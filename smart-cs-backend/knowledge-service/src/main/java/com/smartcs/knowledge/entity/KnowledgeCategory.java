package com.smartcs.knowledge.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cs_kb_category")
public class KnowledgeCategory {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long parentId;
    
    private String categoryName;
    
    private String categoryPath;
    
    private Integer level;
    
    private Integer sortOrder;
    
    private Integer status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
