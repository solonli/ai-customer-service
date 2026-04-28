package com.smartcs.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cs_role")
public class Role {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String roleName;
    
    private String roleCode;
    
    private String description;
    
    private Integer dataScope;
    
    private Integer status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
