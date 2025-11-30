package com.ddlab.rnd.snyk.ai.out.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SnykFixInfo {
    @JsonProperty("isFixable")
    private Boolean isFixable;
    @JsonProperty("fixedIn")
    private List<String> fixedIn;
}
