package com.smartcs.knowledge.controller;

import com.smartcs.common.core.result.PageResult;
import com.smartcs.common.core.result.Result;
import com.smartcs.knowledge.dto.*;
import com.smartcs.knowledge.service.KnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "知识库管理", description = "知识CRUD、检索等接口")
@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @Operation(summary = "创建知识条目", description = "创建新的知识条目")
    @PostMapping
    public Result<KnowledgeEntryDTO> createKnowledge(@RequestBody CreateKnowledgeRequest request,
                                                    @RequestHeader("userId") Long userId) {
        KnowledgeEntryDTO dto = knowledgeService.createKnowledge(request, userId);
        return Result.success(dto);
    }

    @Operation(summary = "获取知识详情", description = "根据ID获取知识条目详情")
    @GetMapping("/{id}")
    public Result<KnowledgeEntryDTO> getKnowledge(@PathVariable Long id) {
        KnowledgeEntryDTO dto = knowledgeService.getKnowledgeById(id);
        return Result.success(dto);
    }

    @Operation(summary = "获取知识列表", description = "分页获取知识条目列表")
    @GetMapping
    public Result<PageResult<KnowledgeEntryDTO>> getKnowledgeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        PageResult<KnowledgeEntryDTO> result = knowledgeService.getKnowledgeList(page - 1, size, categoryId, status);
        return Result.success(result);
    }

    @Operation(summary = "发布知识", description = "将草稿状态的知识发布")
    @PostMapping("/{id}/publish")
    public Result<KnowledgeEntryDTO> publishKnowledge(@PathVariable Long id,
                                                      @RequestHeader("userId") Long userId) {
        KnowledgeEntryDTO dto = knowledgeService.publishKnowledge(id, userId);
        return Result.success(dto);
    }

    @Operation(summary = "删除知识", description = "删除指定知识条目")
    @DeleteMapping("/{id}")
    public Result<Void> deleteKnowledge(@PathVariable Long id) {
        knowledgeService.deleteKnowledge(id);
        return Result.success();
    }

    @Operation(summary = "知识检索", description = "根据关键词搜索知识")
    @PostMapping("/search")
    public Result<SearchKnowledgeResult> searchKnowledge(@RequestBody SearchKnowledgeRequest request) {
        SearchKnowledgeResult result = knowledgeService.searchKnowledge(request);
        return Result.success(result);
    }
}
