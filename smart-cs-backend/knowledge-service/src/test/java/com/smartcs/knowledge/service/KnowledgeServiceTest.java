package com.smartcs.knowledge.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartcs.knowledge.dto.CreateKnowledgeRequest;
import com.smartcs.knowledge.dto.KnowledgeEntryDTO;
import com.smartcs.knowledge.dto.SearchKnowledgeRequest;
import com.smartcs.knowledge.dto.SearchKnowledgeResult;
import com.smartcs.knowledge.entity.KnowledgeEntry;
import com.smartcs.knowledge.mapper.KnowledgeEntryMapper;
import com.smartcs.common.core.result.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("知识库服务测试")
class KnowledgeServiceTest {

    @Mock
    private KnowledgeEntryMapper entryMapper;

    @InjectMocks
    private KnowledgeService knowledgeService;

    private KnowledgeEntry testEntry;

    @BeforeEach
    void setUp() {
        testEntry = new KnowledgeEntry();
        testEntry.setId(1L);
        testEntry.setEntryNo("E20240115001");
        testEntry.setTitle("如何查询订单");
        testEntry.setContent("登录账户后，点击我的订单即可查看");
        testEntry.setCategoryId(1L);
        testEntry.setStatus(1);
        testEntry.setVersion(1);
        testEntry.setEffectiveCount(10);
        testEntry.setIneffectiveCount(2);
        testEntry.setCreatedAt(LocalDateTime.now());
        testEntry.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("创建知识条目测试")
    void testCreateKnowledge() {
        CreateKnowledgeRequest request = new CreateKnowledgeRequest();
        request.setTitle("如何退款");
        request.setContent("在订单详情页点击退款按钮");
        request.setCategoryId(1L);

        when(entryMapper.insert(any(KnowledgeEntry.class))).thenReturn(1);

        KnowledgeEntryDTO result = knowledgeService.createKnowledge(request, 1L);

        assertNotNull(result, "创建结果不应为空");
        verify(entryMapper, times(1)).insert(any(KnowledgeEntry.class));
    }

    @Test
    @DisplayName("获取知识条目测试")
    void testGetKnowledgeById() {
        when(entryMapper.selectById(1L)).thenReturn(testEntry);

        KnowledgeEntryDTO result = knowledgeService.getKnowledgeById(1L);

        assertNotNull(result, "查询结果不应为空");
        assertEquals(testEntry.getTitle(), result.getTitle(), "标题应匹配");
    }

    @Test
    @DisplayName("获取知识条目-不存在")
    void testGetKnowledgeByIdNotFound() {
        when(entryMapper.selectById(999L)).thenReturn(null);

        KnowledgeEntryDTO result = knowledgeService.getKnowledgeById(999L);

        assertNull(result, "不存在的条目应返回null");
    }

    @Test
    @DisplayName("获取知识列表测试")
    void testGetKnowledgeList() {
        Page<KnowledgeEntry> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(testEntry));
        page.setTotal(1);

        when(entryMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        PageResult<KnowledgeEntryDTO> result = knowledgeService.getKnowledgeList(1, 10, null, null);

        assertNotNull(result, "列表结果不应为空");
        assertEquals(1, result.getTotal(), "总数应为1");
        assertEquals(1, result.getRecords().size(), "记录数应为1");
    }

    @Test
    @DisplayName("发布知识条目测试")
    void testPublishKnowledge() {
        KnowledgeEntry draftEntry = new KnowledgeEntry();
        draftEntry.setId(1L);
        draftEntry.setTitle("测试知识");
        draftEntry.setStatus(0);

        when(entryMapper.selectById(1L)).thenReturn(draftEntry);
        when(entryMapper.updateById(any(KnowledgeEntry.class))).thenReturn(1);

        KnowledgeEntryDTO result = knowledgeService.publishKnowledge(1L, 1L);

        assertNotNull(result, "发布结果不应为空");
        verify(entryMapper, times(1)).updateById(any(KnowledgeEntry.class));
    }

    @Test
    @DisplayName("发布知识条目-不存在")
    void testPublishKnowledgeNotFound() {
        when(entryMapper.selectById(999L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            knowledgeService.publishKnowledge(999L, 1L);
        }, "不存在的条目应抛出异常");
    }

    @Test
    @DisplayName("删除知识条目测试")
    void testDeleteKnowledge() {
        when(entryMapper.selectById(1L)).thenReturn(testEntry);
        when(entryMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> {
            knowledgeService.deleteKnowledge(1L);
        }, "删除操作不应抛出异常");

        verify(entryMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("搜索知识测试")
    void testSearchKnowledge() {
        SearchKnowledgeRequest request = new SearchKnowledgeRequest();
        request.setKeyword("订单");
        request.setPage(1);
        request.setSize(10);

        Page<KnowledgeEntry> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(testEntry));
        page.setTotal(1);

        when(entryMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        SearchKnowledgeResult result = knowledgeService.searchKnowledge(request);

        assertNotNull(result, "搜索结果不应为空");
        assertEquals(1, result.getTotal(), "总数应为1");
    }

    @Test
    @DisplayName("增加有效计数测试")
    void testIncrementEffectiveCount() {
        when(entryMapper.selectById(1L)).thenReturn(testEntry);
        when(entryMapper.updateById(any(KnowledgeEntry.class))).thenReturn(1);

        knowledgeService.incrementEffectiveCount(1L);

        assertEquals(11, testEntry.getEffectiveCount(), "有效计数应增加1");
        verify(entryMapper, times(1)).updateById(any(KnowledgeEntry.class));
    }
}
