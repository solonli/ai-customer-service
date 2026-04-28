package com.smartcs.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建知识条目请求")
public class CreateKnowledgeRequest {
    
    @Schema(description = "分类ID", required = true)
    private Long categoryId;
    
    @Schema(description = "标题/问题", required = true)
    private String title;
    
    @Schema(description = "内容/答案", required = true)
    private String content;
    
    @Schema(description = "内容类型", defaultValue = "text")
    private String contentType = "text";
    
    @Schema(description = "关键词，逗号分隔")
    private String keywords;
    
    @Schema(description = "相似问题，JSON数组")
    private String similarQuestions;
    
    @Schema(description = "关联意图ID")
    private String intentId;
}
