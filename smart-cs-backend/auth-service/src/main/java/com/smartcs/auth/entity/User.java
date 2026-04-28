package com.smartcs.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cs_user")
public class User {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String userNo;
    
    private Integer userType;
    
    private String nickName;
    
    private String realName;
    
    private String phone;
    
    private String phoneHash;
    
    private String email;
    
    private String avatarUrl;
    
    private String password;
    
    private String channel;
    
    private String channelUserId;
    
    private Integer status;
    
    private LocalDateTime lastLoginTime;
    
    private String lastLoginIp;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime deletedAt;
}
