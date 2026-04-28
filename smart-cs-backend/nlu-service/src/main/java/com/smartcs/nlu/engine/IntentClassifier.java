package com.smartcs.nlu.engine;

import com.smartcs.nlu.model.IntentDefinition;
import com.smartcs.nlu.model.NLURequest;
import com.smartcs.nlu.model.NLUResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class IntentClassifier {

    private final Map<String, IntentDefinition> intentRegistry = new HashMap<>();
    
    public IntentClassifier() {
        initDefaultIntents();
    }
    
    private void initDefaultIntents() {
        registerIntent(IntentDefinition.builder()
            .code("greeting")
            .name("问候")
            .keywords(Arrays.asList("你好", "您好", "hi", "hello", "早上好", "下午好", "晚上好"))
            .patterns(Arrays.asList(".*你好.*", ".*您好.*", "^hi.*", "^hello.*"))
            .responses(Arrays.asList("您好！欢迎咨询，有什么可以帮您的吗？", "您好！很高兴为您服务！"))
            .priority(1.0)
            .needClarification(false)
            .build());
            
        registerIntent(IntentDefinition.builder()
            .code("query_order")
            .name("订单查询")
            .keywords(Arrays.asList("订单", "查询订单", "查订单", "订单号", "物流", "快递"))
            .patterns(Arrays.asList(".*订单.*查询.*", ".*查.*订单.*", ".*物流.*", ".*快递.*"))
            .responses(Arrays.asList("好的，请提供您的订单号，我来帮您查询。", "请告诉我订单号，我来帮您查询订单状态。"))
            .priority(0.9)
            .needClarification(false)
            .build());
            
        registerIntent(IntentDefinition.builder()
            .code("refund_request")
            .name("退款申请")
            .keywords(Arrays.asList("退款", "退货", "退钱", "退单", "申请退款"))
            .patterns(Arrays.asList(".*退款.*", ".*退货.*", ".*退.*款.*"))
            .responses(Arrays.asList("好的，我来帮您处理退款。请提供订单号。", "退款流程：1.找到我的订单 2.选择需要退款的商品 3.点击退款按钮 4.填写退款原因并提交。"))
            .priority(0.9)
            .needClarification(false)
            .build());
            
        registerIntent(IntentDefinition.builder()
            .code("product_inquiry")
            .name("商品咨询")
            .keywords(Arrays.asList("商品", "产品", "价格", "多少钱", "有货", "库存", "规格"))
            .patterns(Arrays.asList(".*商品.*", ".*产品.*", ".*价格.*", ".*多少钱.*"))
            .responses(Arrays.asList("请问您想了解哪个商品的信息？", "请告诉我您感兴趣的商品名称。"))
            .priority(0.85)
            .needClarification(true)
            .build());
            
        registerIntent(IntentDefinition.builder()
            .code("complaint")
            .name("投诉建议")
            .keywords(Arrays.asList("投诉", "不满意", "差评", "举报", "建议", "意见"))
            .patterns(Arrays.asList(".*投诉.*", ".*不满意.*", ".*差评.*", ".*建议.*"))
            .responses(Arrays.asList("非常抱歉给您带来不好的体验，请问具体是什么问题？", "感谢您的反馈，我们会认真处理您的投诉。"))
            .priority(0.95)
            .needClarification(false)
            .transferTo("human")
            .build());
            
        registerIntent(IntentDefinition.builder()
            .code("transfer_human")
            .name("转人工")
            .keywords(Arrays.asList("人工", "客服", "人工客服", "转人工", "真人"))
            .patterns(Arrays.asList(".*人工.*", ".*转人工.*", ".*真人.*"))
            .responses(Arrays.asList("正在为您转接人工客服，请稍候...", "好的，马上为您转接人工客服。"))
            .priority(1.0)
            .needClarification(false)
            .transferTo("human")
            .build());
            
        registerIntent(IntentDefinition.builder()
            .code("farewell")
            .name("告别")
            .keywords(Arrays.asList("再见", "拜拜", "bye", "好的谢谢", "没事了"))
            .patterns(Arrays.asList(".*再见.*", ".*拜拜.*", "^bye.*", ".*谢谢.*"))
            .responses(Arrays.asList("感谢您的咨询，再见！", "很高兴为您服务，再见！"))
            .priority(0.8)
            .needClarification(false)
            .build());
            
        registerIntent(IntentDefinition.builder()
            .code("thanks")
            .name("感谢")
            .keywords(Arrays.asList("谢谢", "感谢", "thanks", "thank"))
            .patterns(Arrays.asList(".*谢谢.*", ".*感谢.*", "^thanks.*", "^thank.*"))
            .responses(Arrays.asList("不客气，很高兴能帮到您！", "这是我们应该做的，还有其他问题吗？"))
            .priority(0.7)
            .needClarification(false)
            .build());
            
        log.info("初始化意图定义完成，共{}个意图", intentRegistry.size());
    }
    
    public void registerIntent(IntentDefinition intent) {
        intentRegistry.put(intent.getCode(), intent);
    }
    
    public List<NLUResponse.Intent> classify(String text) {
        List<ScoredIntent> scoredIntents = new ArrayList<>();
        
        for (IntentDefinition intent : intentRegistry.values()) {
            double score = calculateIntentScore(text, intent);
            if (score > 0) {
                scoredIntents.add(new ScoredIntent(intent, score));
            }
        }
        
        scoredIntents.sort((a, b) -> Double.compare(b.score, a.score));
        
        return scoredIntents.stream()
            .limit(3)
            .map(this::toIntentResponse)
            .collect(Collectors.toList());
    }
    
    private double calculateIntentScore(String text, IntentDefinition intent) {
        double score = 0;
        
        for (String keyword : intent.getKeywords()) {
            if (text.contains(keyword)) {
                score += 0.3;
            }
        }
        
        for (String pattern : intent.getPatterns()) {
            try {
                Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(text);
                if (m.find()) {
                    score += 0.5;
                }
            } catch (Exception e) {
                log.warn("正则匹配失败: pattern={}", pattern);
            }
        }
        
        score *= intent.getPriority();
        
        return Math.min(score, 1.0);
    }
    
    private NLUResponse.Intent toIntentResponse(ScoredIntent scored) {
        NLUResponse.Intent intent = new NLUResponse.Intent();
        intent.setCode(scored.intent.getCode());
        intent.setName(scored.intent.getName());
        intent.setConfidence(scored.score);
        return intent;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class ScoredIntent {
        private IntentDefinition intent;
        private double score;
    }
}
