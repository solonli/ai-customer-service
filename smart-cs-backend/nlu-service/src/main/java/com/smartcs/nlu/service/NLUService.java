package com.smartcs.nlu.service;

import com.smartcs.nlu.engine.EntityExtractor;
import com.smartcs.nlu.engine.IntentClassifier;
import com.smartcs.nlu.engine.KeywordExtractor;
import com.smartcs.nlu.engine.SentimentAnalyzer;
import com.smartcs.nlu.model.NLURequest;
import com.smartcs.nlu.model.NLUResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NLUService {

    private final IntentClassifier intentClassifier;
    private final EntityExtractor entityExtractor;
    private final SentimentAnalyzer sentimentAnalyzer;
    private final KeywordExtractor keywordExtractor;
    
    public NLUResponse analyze(NLURequest request) {
        log.info("开始NLU分析: text={}", request.getText());
        
        long startTime = System.currentTimeMillis();
        
        NLUResponse response = new NLUResponse();
        
        List<NLUResponse.Intent> intents = intentClassifier.classify(request.getText());
        response.setIntents(intents);
        
        if (!intents.isEmpty()) {
            response.setTopIntent(intents.get(0));
            response.setConfidence(intents.get(0).getConfidence());
        }
        
        List<NLUResponse.Entity> entities = entityExtractor.extract(request.getText());
        response.setEntities(entities);
        
        List<String> keywords = keywordExtractor.extract(request.getText());
        response.setKeywords(keywords);
        
        NLUResponse.Sentiment sentiment = sentimentAnalyzer.analyze(request.getText());
        response.setSentiment(sentiment);
        
        response.setMetadata(new HashMap<>());
        response.getMetadata().put("processingTimeMs", System.currentTimeMillis() - startTime);
        response.getMetadata().put("textLength", request.getText().length());
        
        log.info("NLU分析完成: intent={}, confidence={}, entities={}, keywords={}", 
            response.getTopIntent() != null ? response.getTopIntent().getCode() : null,
            response.getConfidence(),
            entities.size(),
            keywords.size());
        
        return response;
    }
    
    public NLUResponse.Intent getIntentByCode(String code) {
        NLUResponse.Intent intent = new NLUResponse.Intent();
        intent.setCode(code);
        return intent;
    }
}
