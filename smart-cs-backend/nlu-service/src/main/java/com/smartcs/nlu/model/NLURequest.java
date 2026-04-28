package com.smartcs.nlu.model;

import lombok.Data;
import java.util.Map;
import java.util.List;

@Data
public class NLURequest {
    private String text;
    private String sessionId;
    private String userId;
    private Map<String, Object> context;
}
