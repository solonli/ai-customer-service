package com.smartcs.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "知识检索请求")
public class SearchKnowledgeRequest {
    
    @Schema(description = "搜索关键词", required = true)
    private String keyword;
    
    @Schema(description = "分类ID过滤")
    private Long categoryId;
    
    @Schema(description = "分页大小")
    private Integer size = 10;
    
    @Schema(description = "页码")
    private Integer page = 0;
}
