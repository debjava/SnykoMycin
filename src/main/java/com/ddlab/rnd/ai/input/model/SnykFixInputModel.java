package com.ddlab.rnd.ai.input.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter //@AllArgsConstructor @NoArgsConstructor
public class SnykFixInputModel {

    private String artifactName;
    private List<String> fixedVersions; // <fixVersion>

}
