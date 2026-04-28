package com.smartcs.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "知识条目DTO")
public class KnowledgeEntryDTO {
    
    @Schema(description = "条目ID")
    private Long id;
    
    @Schema(description = "条目编号")
    private String entryNo;
    
    @Schema(description = "分类ID")
    private Long categoryId;
    
    @Schema(description = "标题/问题")
    private String title;
    
    @Schema(description = "内容/答案")
    private String content;
    
    @Schema(description = "内容类型")
    private String contentType;
    
    @Schema(description = "关键词")
    private String keywords;
    
    @Schema(description = "状态：0-草稿，1-已发布，2-已下架")
    private Integer status;
    
    @Schema(description = "有效命中次数")
    private Integer effectiveCount;
    
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
