package com.smartcs.nlu.engine;

import com.smartcs.nlu.model.NLUResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("情感分析器测试")
class SentimentAnalyzerTest {

    private SentimentAnalyzer sentimentAnalyzer;

    @BeforeEach
    void setUp() {
        sentimentAnalyzer = new SentimentAnalyzer();
    }

    @Test
    @DisplayName("测试积极情感")
    void testPositiveSentiment() {
        String text = "非常感谢你们的帮助，服务很好！";
        NLUResponse.Sentiment sentiment = sentimentAnalyzer.analyze(text);
        
        assertEquals("positive", sentiment.getLabel(), "应识别为积极情感");
        assertTrue(sentiment.getPositive() > sentiment.getNegative(), 
            "积极分数应大于消极分数");
    }

    @Test
    @DisplayName("测试消极情感")
    void testNegativeSentiment() {
        String text = "太差了，我很不满意，要投诉！";
        NLUResponse.Sentiment sentiment = sentimentAnalyzer.analyze(text);
        
        assertEquals("negative", sentiment.getLabel(), "应识别为消极情感");
        assertTrue(sentiment.getNegative() > sentiment.getPositive(), 
            "消极分数应大于积极分数");
    }

    @Test
    @DisplayName("测试中性情感")
    void testNeutralSentiment() {
        String text = "请问这个商品有货吗？";
        NLUResponse.Sentiment sentiment = sentimentAnalyzer.analyze(text);
        
        assertEquals("neutral", sentiment.getLabel(), "应识别为中性情感");
    }

    @Test
    @DisplayName("测试程度副词影响")
    void testIntensifierEffect() {
        String normalText = "服务好";
        String intensifiedText = "服务非常好";
        
        NLUResponse.Sentiment normal = sentimentAnalyzer.analyze(normalText);
        NLUResponse.Sentiment intensified = sentimentAnalyzer.analyze(intensifiedText);
        
        assertTrue(intensified.getPositive() >= normal.getPositive(), 
            "带程度副词的情感分数应更高或相等");
    }
}
