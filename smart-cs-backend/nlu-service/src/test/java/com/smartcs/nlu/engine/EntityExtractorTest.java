package com.smartcs.nlu.engine;

import com.smartcs.nlu.model.NLUResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("实体提取器测试")
class EntityExtractorTest {

    private EntityExtractor entityExtractor;

    @BeforeEach
    void setUp() {
        entityExtractor = new EntityExtractor();
    }

    @Test
    @DisplayName("测试订单号提取")
    void testOrderIdExtraction() {
        String text = "我的订单号是JD20240115001，请帮我查一下";
        List<NLUResponse.Entity> entities = entityExtractor.extract(text);
        
        assertFalse(entities.isEmpty(), "应提取到实体");
        assertTrue(entities.stream().anyMatch(e -> "order_id".equals(e.getType())), 
            "应包含订单号实体");
    }

    @Test
    @DisplayName("测试手机号提取")
    void testPhoneExtraction() {
        String text = "我的手机号是13812345678";
        List<NLUResponse.Entity> entities = entityExtractor.extract(text);
        
        assertFalse(entities.isEmpty(), "应提取到实体");
        assertTrue(entities.stream().anyMatch(e -> "phone".equals(e.getType())), 
            "应包含手机号实体");
    }

    @Test
    @DisplayName("测试金额提取")
    void testMoneyExtraction() {
        String text = "这个商品价格是99.9元";
        List<NLUResponse.Entity> entities = entityExtractor.extract(text);
        
        assertFalse(entities.isEmpty(), "应提取到实体");
        assertTrue(entities.stream().anyMatch(e -> "money".equals(e.getType())), 
            "应包含金额实体");
    }

    @Test
    @DisplayName("测试日期提取")
    void testDateExtraction() {
        String text = "我2024年1月15日下的单";
        List<NLUResponse.Entity> entities = entityExtractor.extract(text);
        
        assertFalse(entities.isEmpty(), "应提取到实体");
        assertTrue(entities.stream().anyMatch(e -> "date".equals(e.getType())), 
            "应包含日期实体");
    }

    @Test
    @DisplayName("测试行为实体提取")
    void testActionEntityExtraction() {
        String text = "我要申请退款";
        List<NLUResponse.Entity> entities = entityExtractor.extract(text);
        
        assertTrue(entities.stream().anyMatch(e -> 
            "action".equals(e.getType()) && "refund".equals(e.getValue())), 
            "应包含退款行为实体");
    }

    @Test
    @DisplayName("测试空文本")
    void testEmptyText() {
        List<NLUResponse.Entity> entities = entityExtractor.extract("");
        assertTrue(entities.isEmpty(), "空文本不应提取到实体");
    }
}
