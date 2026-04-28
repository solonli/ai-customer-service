package com.smartcs.nlu.service;

import com.smartcs.nlu.engine.EntityExtractor;
import com.smartcs.nlu.engine.IntentClassifier;
import com.smartcs.nlu.engine.KeywordExtractor;
import com.smartcs.nlu.engine.SentimentAnalyzer;
import com.smartcs.nlu.model.NLURequest;
import com.smartcs.nlu.model.NLUResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NLU服务测试")
class NLUServiceTest {

    private NLUService nluService;

    @BeforeEach
    void setUp() {
        IntentClassifier intentClassifier = new IntentClassifier();
        EntityExtractor entityExtractor = new EntityExtractor();
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
        KeywordExtractor keywordExtractor = new KeywordExtractor();
        
        nluService = new NLUService(
            intentClassifier, 
            entityExtractor, 
            sentimentAnalyzer, 
            keywordExtractor
        );
    }

    @Test
    @DisplayName("测试完整NLU分析")
    void testFullAnalysis() {
        NLURequest request = new NLURequest();
        request.setText("你好，我想查询订单JD20240115001");
        
        NLUResponse response = nluService.analyze(request);
        
        assertNotNull(response, "响应不应为空");
        assertNotNull(response.getTopIntent(), "应识别出意图");
        assertNotNull(response.getEntities(), "实体列表不应为空");
        assertNotNull(response.getKeywords(), "关键词列表不应为空");
        assertNotNull(response.getSentiment(), "情感分析结果不应为空");
        assertNotNull(response.getConfidence(), "置信度不应为空");
    }

    @Test
    @DisplayName("测试意图识别准确性")
    void testIntentAccuracy() {
        NLURequest request = new NLURequest();
        request.setText("转人工");
        
        NLUResponse response = nluService.analyze(request);
        
        assertEquals("transfer_human", response.getTopIntent().getCode(), 
            "应正确识别转人工意图");
        assertTrue(response.getConfidence() > 0.5, "置信度应大于0.5");
    }

    @Test
    @DisplayName("测试关键词提取")
    void testKeywordExtraction() {
        NLURequest request = new NLURequest();
        request.setText("我想查询订单的物流信息");
        
        NLUResponse response = nluService.analyze(request);
        
        assertTrue(response.getKeywords().contains("订单") || 
                   response.getKeywords().contains("物流"), 
            "关键词应包含订单或物流");
    }

    @Test
    @DisplayName("测试处理时间记录")
    void testProcessingTimeRecorded() {
        NLURequest request = new NLURequest();
        request.setText("测试文本");
        
        NLUResponse response = nluService.analyze(request);
        
        assertTrue(response.getMetadata().containsKey("processingTimeMs"), 
            "应记录处理时间");
        Long processingTime = (Long) response.getMetadata().get("processingTimeMs");
        assertTrue(processingTime >= 0, "处理时间应大于等于0");
    }
}
