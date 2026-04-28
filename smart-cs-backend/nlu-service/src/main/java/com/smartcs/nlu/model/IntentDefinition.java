package com.smartcs.nlu.model;

import lombok.Data;
import java.util.List;

@Data
public class IntentDefinition {
    private String code;
    private String name;
    private String description;
    private List<String> keywords;
    private List<String> patterns;
    private List<String> responses;
    private Double priority;
    private Boolean needClarification;
    private String transferTo;
}
