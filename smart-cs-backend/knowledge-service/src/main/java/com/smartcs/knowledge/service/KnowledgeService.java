package com.smartcs.knowledge.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcs.knowledge.dto.*;
import com.smartcs.knowledge.entity.KnowledgeEntry;
import com.smartcs.knowledge.mapper.KnowledgeEntryMapper;
import com.smartcs.common.core.result.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final KnowledgeEntryMapper entryMapper;
    
    public KnowledgeEntryDTO createKnowledge(CreateKnowledgeRequest request, Long operatorId) {
        KnowledgeEntry entry = new KnowledgeEntry();
        BeanUtils.copyProperties(request, entry);
        entry.setEntryNo(generateEntryNo());
        entry.setStatus(0);
        entry.setVersion(1);
        entry.setEffectiveCount(0);
        entry.setIneffectiveCount(0);
        entry.setCreatedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());
        
        entryMapper.insert(entry);
        
        log.info("创建知识条目成功: entryId={}, title={}", entry.getId(), entry.getTitle());
        
        return convertToDTO(entry);
    }
    
    public KnowledgeEntryDTO getKnowledgeById(Long id) {
        KnowledgeEntry entry = entryMapper.selectById(id);
        return entry != null ? convertToDTO(entry) : null;
    }
    
    public PageResult<KnowledgeEntryDTO> getKnowledgeList(Integer page, Integer size, Long categoryId, Integer status) {
        Page<KnowledgeEntry> pageParams = new Page<>(page, size);
        
        LambdaQueryWrapper<KnowledgeEntry> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(KnowledgeEntry::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(KnowledgeEntry::getStatus, status);
        }
        wrapper.orderByDesc(KnowledgeEntry::getCreatedAt);
        
        Page<KnowledgeEntry> resultPage = entryMapper.selectPage(pageParams, wrapper);
        
        List<KnowledgeEntryDTO> dtos = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResult.of(dtos, resultPage.getTotal(), resultPage.getSize(), resultPage.getCurrent());
    }
    
    public KnowledgeEntryDTO publishKnowledge(Long id, Long operatorId) {
        KnowledgeEntry entry = entryMapper.selectById(id);
        if (entry == null) {
            throw new RuntimeException("知识条目不存在");
        }
        
        entry.setStatus(1);
        entry.setPublisherId(operatorId);
        entry.setPublishedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());
        
        entryMapper.updateById(entry);
        
        log.info("发布知识条目成功: entryId={}", id);
        
        return convertToDTO(entry);
    }
    
    public void deleteKnowledge(Long id) {
        KnowledgeEntry entry = entryMapper.selectById(id);
        if (entry == null) {
            throw new RuntimeException("知识条目不存在");
        }
        
        entryMapper.deleteById(id);
        
        log.info("删除知识条目成功: entryId={}", id);
    }
    
    public SearchKnowledgeResult searchKnowledge(SearchKnowledgeRequest request) {
        String keyword = request.getKeyword();
        
        Page<KnowledgeEntry> pageParams = new Page<>(request.getPage(), request.getSize());
        
        LambdaQueryWrapper<KnowledgeEntry> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(KnowledgeEntry::getStatus, 1);
        
        if (request.getCategoryId() != null) {
            wrapper.eq(KnowledgeEntry::getCategoryId, request.getCategoryId());
        }
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                .like(KnowledgeEntry::getTitle, keyword)
                .or()
                .like(KnowledgeEntry::getContent, keyword)
                .or()
                .like(KnowledgeEntry::getKeywords, keyword)
            );
        }
        
        wrapper.orderByDesc(KnowledgeEntry::getEffectiveCount);
        
        Page<KnowledgeEntry> resultPage = entryMapper.selectPage(pageParams, wrapper);
        
        List<KnowledgeEntryDTO> dtos = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        SearchKnowledgeResult result = new SearchKnowledgeResult();
        result.setRecords(dtos);
        result.setTotal(resultPage.getTotal());
        
        return result;
    }
    
    public void incrementEffectiveCount(Long id) {
        KnowledgeEntry entry = entryMapper.selectById(id);
        if (entry != null) {
            entry.setEffectiveCount(entry.getEffectiveCount() + 1);
            entry.setUpdatedAt(LocalDateTime.now());
            entryMapper.updateById(entry);
        }
    }
    
    private KnowledgeEntryDTO convertToDTO(KnowledgeEntry entry) {
        KnowledgeEntryDTO dto = new KnowledgeEntryDTO();
        BeanUtils.copyProperties(entry, dto);
        return dto;
    }
    
    private String generateEntryNo() {
        return "E" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
