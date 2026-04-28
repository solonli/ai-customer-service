package com.smartcs.operation.controller;

import com.smartcs.common.core.result.PageResult;
import com.smartcs.common.core.result.Result;
import com.smartcs.operation.dto.CreateTicketRequest;
import com.smartcs.operation.dto.TicketDTO;
import com.smartcs.operation.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "工单管理", description = "工单CRUD、分配、状态更新等接口")
@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "创建工单", description = "创建新工单")
    @PostMapping
    public Result<TicketDTO> createTicket(@RequestBody CreateTicketRequest request,
                                         @RequestHeader("userId") Long userId) {
        TicketDTO dto = ticketService.createTicket(request, userId);
        return Result.success(dto);
    }

    @Operation(summary = "获取工单详情", description = "根据ID获取工单详情")
    @GetMapping("/{id}")
    public Result<TicketDTO> getTicket(@PathVariable Long id) {
        TicketDTO dto = ticketService.getTicketById(id);
        return Result.success(dto);
    }

    @Operation(summary = "获取工单列表", description = "分页获取工单列表")
    @GetMapping
    public Result<PageResult<TicketDTO>> getTicketList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long handlerId) {
        PageResult<TicketDTO> result = ticketService.getTicketList(page - 1, size, status, userId, handlerId);
        return Result.success(result);
    }

    @Operation(summary = "分配工单", description = "将工单分配给指定处理人")
    @PostMapping("/{id}/assign")
    public Result<TicketDTO> assignTicket(@PathVariable Long id,
                                         @RequestParam Long handlerId,
                                         @RequestHeader("userId") Long userId) {
        TicketDTO dto = ticketService.assignTicket(id, handlerId, userId);
        return Result.success(dto);
    }

    @Operation(summary = "更新工单状态", description = "更新工单状态")
    @PostMapping("/{id}/status")
    public Result<TicketDTO> updateTicketStatus(@PathVariable Long id,
                                               @RequestParam Integer status,
                                               @RequestHeader("userId") Long userId) {
        TicketDTO dto = ticketService.updateTicketStatus(id, status, userId);
        return Result.success(dto);
    }

    @Operation(summary = "删除工单", description = "删除指定工单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return Result.success();
    }
}
