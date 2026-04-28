package com.smartcs.nlu.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class NLUResponse {
    private Intent topIntent;
    private List<Intent> intents;
    private List<Entity> entities;
    private List<String> keywords;
    private Sentiment sentiment;
    private Double confidence;
    private Map<String, Object> metadata;

    @Data
    public static class Intent {
        private String code;
        private String name;
        private Double confidence;
        private Map<String, Object> slots;
    }

    @Data
    public static class Entity {
        private String type;
        private String value;
        private Integer start;
        private Integer end;
        private Double confidence;
    }

    @Data
    public static class Sentiment {
        private String label;
        private Double score;
        private Double positive;
        private Double negative;
    }
}
