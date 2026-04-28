package com.smartcs.nlu.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class KeywordExtractor {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "的", "了", "是", "在", "我", "有", "和", "就", "不", "人", "都", "一", "一个",
        "上", "也", "很", "到", "说", "要", "去", "你", "会", "着", "没有", "看", "好",
        "自己", "这", "那", "什么", "怎么", "吗", "呢", "啊", "吧", "呀", "嗯", "哦",
        "可以", "能", "想", "知道", "请问", "麻烦", "帮", "帮忙", "一下", "这个", "那个"
    ));
    
    private static final Set<String> DOMAIN_KEYWORDS = new HashSet<>(Arrays.asList(
        "订单", "退款", "退货", "物流", "快递", "商品", "产品", "价格", "优惠", "折扣",
        "会员", "积分", "优惠券", "红包", "支付", "发货", "收货", "评价", "投诉", "客服",
        "人工", "账户", "密码", "登录", "注册", "绑定", "解绑", "修改", "更换", "取消"
    ));
    
    public List<String> extract(String text) {
        List<String> keywords = new ArrayList<>();
        
        for (String domainKeyword : DOMAIN_KEYWORDS) {
            if (text.contains(domainKeyword)) {
                keywords.add(domainKeyword);
            }
        }
        
        List<String> words = segmentText(text);
        for (String word : words) {
            if (!STOP_WORDS.contains(word) && word.length() >= 2) {
                if (!keywords.contains(word)) {
                    keywords.add(word);
                }
            }
        }
        
        keywords.addAll(extractNumbers(text));
        
        return keywords.stream().distinct().limit(10).collect(Collectors.toList());
    }
    
    private List<String> segmentText(String text) {
        List<String> words = new ArrayList<>();
        
        for (int i = 0; i < text.length() - 1; i++) {
            String bigram = text.substring(i, i + 2);
            if (!containsStopWord(bigram)) {
                words.add(bigram);
            }
        }
        
        return words;
    }
    
    private boolean containsStopWord(String text) {
        for (String stopWord : STOP_WORDS) {
            if (text.contains(stopWord)) {
                return true;
            }
        }
        return false;
    }
    
    private List<String> extractNumbers(String text) {
        List<String> numbers = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);
        
        while (matcher.find()) {
            numbers.add(matcher.group());
        }
        
        return numbers;
    }
}
