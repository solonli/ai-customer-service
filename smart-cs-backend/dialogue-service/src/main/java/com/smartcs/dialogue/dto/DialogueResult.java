package com.smartcs.dialogue.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "对话处理结果")
public class DialogueResult {
    
    @Schema(description = "是否成功")
    private Boolean success;
    
    @Schema(description = "对话策略")
    private String strategy;
    
    @Schema(description = "回复内容")
    private String replyContent;
    
    @Schema(description = "关联知识ID")
    private Long knowledgeId;
    
    @Schema(description = "NLU解析结果")
    private NLUResult nluResult;
    
    @Schema(description = "提示信息")
    private String message;
    
    public static DialogueResult directAnswer(String replyContent, Long knowledgeId, NLUResult nluResult) {
        DialogueResult result = new DialogueResult();
        result.setSuccess(true);
        result.setStrategy("DIRECT_ANSWER");
        result.setReplyContent(replyContent);
        result.setKnowledgeId(knowledgeId);
        result.setNluResult(nluResult);
        return result;
    }
    
    public static DialogueResult clarify(String question, NLUResult nluResult) {
        DialogueResult result = new DialogueResult();
        result.setSuccess(true);
        result.setStrategy("CLARIFY");
        result.setReplyContent(question);
        result.setNluResult(nluResult);
        return result;
    }
    
    public static DialogueResult transfer(String reason, NLUResult nluResult) {
        DialogueResult result = new DialogueResult();
        result.setSuccess(true);
        result.setStrategy("TRANSFER_HUMAN");
        result.setMessage(reason);
        result.setNluResult(nluResult);
        return result;
    }
}
