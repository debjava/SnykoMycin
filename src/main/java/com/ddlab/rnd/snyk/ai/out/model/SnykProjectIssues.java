package com.ddlab.rnd.snyk.ai.out.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SnykProjectIssues {
    @JsonProperty("issues")
    private List<SnykIssue> issues;
}
