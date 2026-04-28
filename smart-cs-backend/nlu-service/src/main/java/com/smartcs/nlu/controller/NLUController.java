package com.smartcs.nlu.controller;

import com.smartcs.common.core.result.Result;
import com.smartcs.nlu.model.NLURequest;
import com.smartcs.nlu.model.NLUResponse;
import com.smartcs.nlu.service.NLUService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "NLU服务", description = "自然语言理解相关接口")
@RestController
@RequestMapping("/api/v1/nlu")
@RequiredArgsConstructor
public class NLUController {

    private final NLUService nluService;
    
    @Operation(summary = "文本分析", description = "对用户输入进行意图识别、实体提取、情感分析等")
    @PostMapping("/analyze")
    public Result<NLUResponse> analyze(@RequestBody NLURequest request) {
        NLUResponse response = nluService.analyze(request);
        return Result.success(response);
    }
    
    @Operation(summary = "意图识别", description = "仅进行意图识别")
    @PostMapping("/intent")
    public Result<NLUResponse> recognizeIntent(@RequestBody NLURequest request) {
        NLUResponse response = new NLUResponse();
        response.setIntents(nluService.analyze(request).getIntents());
        if (!response.getIntents().isEmpty()) {
            response.setTopIntent(response.getIntents().get(0));
        }
        return Result.success(response);
    }
    
    @Operation(summary = "实体提取", description = "仅进行实体提取")
    @PostMapping("/entities")
    public Result<NLUResponse> extractEntities(@RequestBody NLURequest request) {
        NLUResponse response = new NLUResponse();
        response.setEntities(nluService.analyze(request).getEntities());
        return Result.success(response);
    }
}
