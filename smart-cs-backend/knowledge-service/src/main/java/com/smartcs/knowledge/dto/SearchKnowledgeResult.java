package com.smartcs.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "知识检索结果")
public class SearchKnowledgeResult {
    
    @Schema(description = "命中记录")
    private List<KnowledgeEntryDTO> records;
    
    @Schema(description = "总数")
    private Long total;
}
