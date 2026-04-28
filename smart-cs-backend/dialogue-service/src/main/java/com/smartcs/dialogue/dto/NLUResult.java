package com.smartcs.dialogue.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Map;

@Data
@Schema(description = "NLU解析结果")
public class NLUResult {
    
    @Schema(description = "顶级意图")
    private IntentInfo topIntent;
    
    @Schema(description = "实体信息")
    private Map<String, Object> entities;
    
    @Schema(description = "关键词列表")
    private String[] keywords;
    
    @Data
    public static class IntentInfo {
        private String intentCode;
        private String intentName;
        private Double confidence;
    }
}
