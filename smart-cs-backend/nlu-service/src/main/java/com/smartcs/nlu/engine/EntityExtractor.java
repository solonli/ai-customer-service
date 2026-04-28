package com.smartcs.nlu.engine;

import com.smartcs.nlu.model.NLUResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class EntityExtractor {

    private final List<EntityPattern> entityPatterns = new ArrayList<>();
    
    public EntityExtractor() {
        initEntityPatterns();
    }
    
    private void initEntityPatterns() {
        entityPatterns.add(new EntityPattern("order_id", "[A-Z]{2}\\d{10,}", "订单号"));
        entityPatterns.add(new EntityPattern("phone", "1[3-9]\\d{9}", "手机号"));
        entityPatterns.add(new EntityPattern("money", "\\d+(\\.\\d{1,2})?元?", "金额"));
        entityPatterns.add(new EntityPattern("date", "\\d{4}[-/年]\\d{1,2}[-/月]\\d{1,2}日?", "日期"));
        entityPatterns.add(new EntityPattern("time", "\\d{1,2}:\\d{2}", "时间"));
        entityPatterns.add(new EntityPattern("product_name", "【[^】]+】|\\[[^\\]]+\\]", "商品名"));
        
        log.info("初始化实体模式完成，共{}种实体类型", entityPatterns.size());
    }
    
    public List<NLUResponse.Entity> extract(String text) {
        List<NLUResponse.Entity> entities = new ArrayList<>();
        
        for (EntityPattern pattern : entityPatterns) {
            try {
                Pattern p = Pattern.compile(pattern.getRegex());
                Matcher m = p.matcher(text);
                
                while (m.find()) {
                    NLUResponse.Entity entity = new NLUResponse.Entity();
                    entity.setType(pattern.getType());
                    entity.setValue(m.group());
                    entity.setStart(m.start());
                    entity.setEnd(m.end());
                    entity.setConfidence(0.9);
                    entities.add(entity);
                }
            } catch (Exception e) {
                log.warn("实体提取失败: type={}", pattern.getType(), e);
            }
        }
        
        entities.addAll(extractContextualEntities(text));
        
        return entities;
    }
    
    private List<NLUResponse.Entity> extractContextualEntities(String text) {
        List<NLUResponse.Entity> entities = new ArrayList<>();
        
        if (text.contains("退款") || text.contains("退货")) {
            NLUResponse.Entity entity = new NLUResponse.Entity();
            entity.setType("action");
            entity.setValue("refund");
            entity.setConfidence(0.85);
            entities.add(entity);
        }
        
        if (text.contains("投诉") || text.contains("举报")) {
            NLUResponse.Entity entity = new NLUResponse.Entity();
            entity.setType("action");
            entity.setValue("complaint");
            entity.setConfidence(0.85);
            entities.add(entity);
        }
        
        if (text.contains("查询") || text.contains("查一下")) {
            NLUResponse.Entity entity = new NLUResponse.Entity();
            entity.setType("action");
            entity.setValue("query");
            entity.setConfidence(0.8);
            entities.add(entity);
        }
        
        return entities;
    }
    
    @lombok.Data
    @lombok.AllArgsConstructor
    private static class EntityPattern {
        private String type;
        private String regex;
        private String description;
    }
}
