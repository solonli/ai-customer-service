package com.smartcs.nlu.engine;

import com.smartcs.nlu.model.NLUResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class SentimentAnalyzer {

    private static final Set<String> POSITIVE_WORDS = new HashSet<>(Arrays.asList(
        "好", "棒", "赞", "满意", "感谢", "谢谢", "开心", "高兴", "喜欢", "优秀",
        "完美", "不错", "很好", "太好了", "厉害", "专业", "耐心", "热情", "及时"
    ));
    
    private static final Set<String> NEGATIVE_WORDS = new HashSet<>(Arrays.asList(
        "差", "烂", "垃圾", "不满", "投诉", "生气", "愤怒", "失望", "讨厌", "糟糕",
        "不好", "太差", "无语", "坑", "骗", "假", "慢", "态度差", "不耐烦", "敷衍"
    ));
    
    private static final Set<String> INTENSIFIERS = new HashSet<>(Arrays.asList(
        "非常", "特别", "很", "太", "超级", "极其", "相当", "十分"
    ));
    
    public NLUResponse.Sentiment analyze(String text) {
        NLUResponse.Sentiment sentiment = new NLUResponse.Sentiment();
        
        int positiveCount = 0;
        int negativeCount = 0;
        double intensity = 1.0;
        
        for (String intensifier : INTENSIFIERS) {
            if (text.contains(intensifier)) {
                intensity = 1.5;
                break;
            }
        }
        
        for (String word : POSITIVE_WORDS) {
            if (text.contains(word)) {
                positiveCount++;
            }
        }
        
        for (String word : NEGATIVE_WORDS) {
            if (text.contains(word)) {
                negativeCount++;
            }
        }
        
        double positiveScore = Math.min(positiveCount * 0.3 * intensity, 1.0);
        double negativeScore = Math.min(negativeCount * 0.3 * intensity, 1.0);
        
        sentiment.setPositive(positiveScore);
        sentiment.setNegative(negativeScore);
        
        if (positiveCount > negativeCount) {
            sentiment.setLabel("positive");
            sentiment.setScore(positiveScore);
        } else if (negativeCount > positiveCount) {
            sentiment.setLabel("negative");
            sentiment.setScore(negativeScore);
        } else {
            sentiment.setLabel("neutral");
            sentiment.setScore(0.5);
        }
        
        return sentiment;
    }
}
