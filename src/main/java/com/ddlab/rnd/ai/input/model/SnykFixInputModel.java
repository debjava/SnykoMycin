package com.ddlab.rnd.ai.input.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter //@AllArgsConstructor @NoArgsConstructor
public class SnykFixInputModel {

    private String artifactName;
    private List<String> fixedVersions; // <fixVersion>

}
