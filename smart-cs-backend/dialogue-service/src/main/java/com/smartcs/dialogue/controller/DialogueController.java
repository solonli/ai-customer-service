package com.smartcs.dialogue.controller;

import com.smartcs.common.core.result.Result;
import com.smartcs.dialogue.dto.DialogueResult;
import com.smartcs.dialogue.dto.ProcessMessageRequest;
import com.smartcs.dialogue.service.DialogueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "对话管理", description = "消息处理、对话策略等接口")
@RestController
@RequestMapping("/api/v1/dialogue")
@RequiredArgsConstructor
public class DialogueController {

    private final DialogueService dialogueService;

    @Operation(summary = "处理用户消息", description = "智能处理用户消息并返回回复")
    @PostMapping("/process")
    public Result<DialogueResult> processMessage(@RequestBody ProcessMessageRequest request) {
        DialogueResult result = dialogueService.processMessage(request);
        return Result.success(result);
    }
}
