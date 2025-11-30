package com.ddlab.rnd.ai.output.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AIResponseModel {
	
	@JsonProperty
	private String id;
	@JsonProperty
    private String object;
	@JsonProperty
    private long created;
	@JsonProperty
    private String model;
    @JsonProperty("choices")
    private List<Choice> choices;
    @JsonProperty
    private Usage usage;

}
