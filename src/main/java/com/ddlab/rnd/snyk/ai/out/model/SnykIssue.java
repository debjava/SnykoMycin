package com.ddlab.rnd.snyk.ai.out.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SnykIssue {

    @JsonProperty("pkgName")
    private String pkgName;
    @JsonProperty("severity")
    private String severity;
    @JsonProperty("pkgVersions")
    private List<String> pkgVersions;
    @JsonProperty("fixInfo")
    private SnykFixInfo fixInfo;
}
