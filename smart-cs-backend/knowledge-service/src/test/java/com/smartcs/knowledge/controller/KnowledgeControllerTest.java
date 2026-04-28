package com.smartcs.knowledge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcs.knowledge.dto.CreateKnowledgeRequest;
import com.smartcs.knowledge.dto.KnowledgeEntryDTO;
import com.smartcs.knowledge.dto.SearchKnowledgeRequest;
import com.smartcs.knowledge.dto.SearchKnowledgeResult;
import com.smartcs.knowledge.service.KnowledgeService;
import com.smartcs.common.core.result.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("知识库控制器测试")
class KnowledgeControllerTest {

    @Mock
    private KnowledgeService knowledgeService;

    @InjectMocks
    private KnowledgeController knowledgeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private KnowledgeEntryDTO testDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(knowledgeController).build();
        objectMapper = new ObjectMapper();

        testDTO = new KnowledgeEntryDTO();
        testDTO.setId(1L);
        testDTO.setEntryNo("E20240115001");
        testDTO.setTitle("如何查询订单");
        testDTO.setContent("登录账户后，点击我的订单即可查看");
        testDTO.setStatus(1);
        testDTO.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("创建知识接口测试")
    void testCreateKnowledgeEndpoint() throws Exception {
        CreateKnowledgeRequest request = new CreateKnowledgeRequest();
        request.setTitle("如何退款");
        request.setContent("在订单详情页点击退款按钮");
        request.setCategoryId(1L);

        when(knowledgeService.createKnowledge(any(CreateKnowledgeRequest.class), anyLong())).thenReturn(testDTO);

        mockMvc.perform(post("/api/v1/knowledge")
                .header("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("如何查询订单"));
    }

    @Test
    @DisplayName("获取知识详情接口测试")
    void testGetKnowledgeEndpoint() throws Exception {
        when(knowledgeService.getKnowledgeById(1L)).thenReturn(testDTO);

        mockMvc.perform(get("/api/v1/knowledge/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("获取知识列表接口测试")
    void testGetKnowledgeListEndpoint() throws Exception {
        PageResult<KnowledgeEntryDTO> pageResult = PageResult.of(Arrays.asList(testDTO), 1L, 10, 1);

        when(knowledgeService.getKnowledgeList(1, 10, null, null)).thenReturn(pageResult);

        mockMvc.perform(get("/api/v1/knowledge")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("发布知识接口测试")
    void testPublishKnowledgeEndpoint() throws Exception {
        when(knowledgeService.publishKnowledge(1L, 1L)).thenReturn(testDTO);

        mockMvc.perform(post("/api/v1/knowledge/1/publish")
                .header("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("删除知识接口测试")
    void testDeleteKnowledgeEndpoint() throws Exception {
        mockMvc.perform(delete("/api/v1/knowledge/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("搜索知识接口测试")
    void testSearchKnowledgeEndpoint() throws Exception {
        SearchKnowledgeRequest request = new SearchKnowledgeRequest();
        request.setKeyword("订单");
        request.setPage(1);
        request.setSize(10);

        SearchKnowledgeResult result = new SearchKnowledgeResult();
        result.setTotal(1L);
        result.setRecords(Arrays.asList(testDTO));

        when(knowledgeService.searchKnowledge(any(SearchKnowledgeRequest.class))).thenReturn(result);

        mockMvc.perform(post("/api/v1/knowledge/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.total").value(1));
    }
}
