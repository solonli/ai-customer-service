package com.smartcs.interaction.controller;

import com.smartcs.common.core.result.Result;
import com.smartcs.interaction.dto.SessionCreateRequest;
import com.smartcs.interaction.dto.SessionDTO;
import com.smartcs.interaction.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "会话管理", description = "会话创建、查询、关闭等接口")
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @Operation(summary = "创建会话", description = "创建新的客服会话")
    @PostMapping
    public Result<SessionDTO> createSession(@RequestBody SessionCreateRequest request) {
        SessionDTO session = sessionService.createSession(request);
        return Result.success(session);
    }

    @Operation(summary = "获取会话信息", description = "根据会话ID获取会话详情")
    @GetMapping("/{id}")
    public Result<SessionDTO> getSession(@PathVariable Long id) {
        SessionDTO session = sessionService.getSessionById(id);
        return Result.success(session);
    }

    @Operation(summary = "关闭会话", description = "关闭指定会话")
    @PutMapping("/{id}/close")
    public Result<Void> closeSession(@PathVariable Long id) {
        sessionService.closeSession(id);
        return Result.success();
    }

    @Operation(summary = "请求转人工", description = "将会话转接给人工客服")
    @PostMapping("/{id}/transfer")
    public Result<Void> transferToAgent(@PathVariable Long id, @RequestParam(required = false) String reason) {
        sessionService.transferToAgent(id, reason);
        return Result.success();
    }
}
