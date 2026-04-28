package com.smartcs.operation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcs.common.core.result.PageResult;
import com.smartcs.operation.dto.CreateTicketRequest;
import com.smartcs.operation.dto.TicketDTO;
import com.smartcs.operation.entity.Ticket;
import com.smartcs.operation.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketMapper ticketMapper;
    
    public TicketDTO createTicket(CreateTicketRequest request, Long operatorId) {
        Ticket ticket = new Ticket();
        BeanUtils.copyProperties(request, ticket);
        ticket.setTicketNo(generateTicketNo());
        ticket.setStatus(0);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        
        ticketMapper.insert(ticket);
        
        log.info("创建工单成功: ticketId={}, ticketNo={}", ticket.getId(), ticket.getTicketNo());
        
        return convertToDTO(ticket);
    }
    
    public TicketDTO getTicketById(Long id) {
        Ticket ticket = ticketMapper.selectById(id);
        return ticket != null ? convertToDTO(ticket) : null;
    }
    
    public PageResult<TicketDTO> getTicketList(Integer page, Integer size, Integer status, Long userId, Long handlerId) {
        Page<Ticket> pageParams = new Page<>(page, size);
        
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Ticket::getStatus, status);
        }
        if (userId != null) {
            wrapper.eq(Ticket::getUserId, userId);
        }
        if (handlerId != null) {
            wrapper.eq(Ticket::getHandlerId, handlerId);
        }
        wrapper.orderByDesc(Ticket::getCreatedAt);
        
        Page<Ticket> resultPage = ticketMapper.selectPage(pageParams, wrapper);
        
        List<TicketDTO> dtos = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(dtos, resultPage.getTotal(), resultPage.getSize(), resultPage.getCurrent());
    }
    
    public TicketDTO assignTicket(Long id, Long handlerId, Long operatorId) {
        Ticket ticket = ticketMapper.selectById(id);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }
        
        ticket.setHandlerId(handlerId);
        ticket.setStatus(1);
        ticket.setUpdatedAt(LocalDateTime.now());
        
        ticketMapper.updateById(ticket);
        
        log.info("分配工单成功: ticketId={}, handlerId={}", id, handlerId);
        
        return convertToDTO(ticket);
    }
    
    public TicketDTO updateTicketStatus(Long id, Integer status, Long operatorId) {
        Ticket ticket = ticketMapper.selectById(id);
        if (ticket == null) {
            throw new RuntimeException("工单不存在");
        }
        
        ticket.setStatus(status);
        if (status == 3) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        if (status == 4) {
            ticket.setClosedAt(LocalDateTime.now());
        }
        ticket.setUpdatedAt(LocalDateTime.now());
        
        ticketMapper.updateById(ticket);
        
        log.info("更新工单状态成功: ticketId={}, status={}", id, status);
        
        return convertToDTO(ticket);
    }
    
    public void deleteTicket(Long id) {
        ticketMapper.deleteById(id);
        
        log.info("删除工单成功: ticketId={}", id);
    }
    
    private TicketDTO convertToDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        BeanUtils.copyProperties(ticket, dto);
        return dto;
    }
    
    private String generateTicketNo() {
        return "TK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
