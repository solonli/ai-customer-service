package com.smartcs.nlu.engine;

import com.smartcs.nlu.model.NLUResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("意图分类器测试")
class IntentClassifierTest {

    private IntentClassifier intentClassifier;

    @BeforeEach
    void setUp() {
        intentClassifier = new IntentClassifier();
    }

    @Test
    @DisplayName("测试问候意图识别")
    void testGreetingIntent() {
        List<NLUResponse.Intent> intents = intentClassifier.classify("你好");
        
        assertFalse(intents.isEmpty(), "意图列表不应为空");
        assertEquals("greeting", intents.get(0).getCode(), "应识别为问候意图");
        assertTrue(intents.get(0).getConfidence() > 0, "置信度应大于0");
    }

    @Test
    @DisplayName("测试订单查询意图识别")
    void testQueryOrderIntent() {
        List<NLUResponse.Intent> intents = intentClassifier.classify("我想查询订单");
        
        assertFalse(intents.isEmpty(), "意图列表不应为空");
        assertEquals("query_order", intents.get(0).getCode(), "应识别为订单查询意图");
    }

    @Test
    @DisplayName("测试退款申请意图识别")
    void testRefundRequestIntent() {
        List<NLUResponse.Intent> intents = intentClassifier.classify("我要退款");
        
        assertFalse(intents.isEmpty(), "意图列表不应为空");
        assertEquals("refund_request", intents.get(0).getCode(), "应识别为退款申请意图");
    }

    @Test
    @DisplayName("测试转人工意图识别")
    void testTransferHumanIntent() {
        List<NLUResponse.Intent> intents = intentClassifier.classify("转人工客服");
        
        assertFalse(intents.isEmpty(), "意图列表不应为空");
        assertEquals("transfer_human", intents.get(0).getCode(), "应识别为转人工意图");
    }

    @Test
    @DisplayName("测试感谢意图识别")
    void testThanksIntent() {
        List<NLUResponse.Intent> intents = intentClassifier.classify("谢谢你的帮助");
        
        assertFalse(intents.isEmpty(), "意图列表不应为空");
        assertEquals("thanks", intents.get(0).getCode(), "应识别为感谢意图");
    }

    @Test
    @DisplayName("测试返回多个意图")
    void testMultipleIntents() {
        List<NLUResponse.Intent> intents = intentClassifier.classify("你好，我想查询订单");
        
        assertFalse(intents.isEmpty(), "意图列表不应为空");
        assertTrue(intents.size() <= 3, "最多返回3个意图");
    }
}
