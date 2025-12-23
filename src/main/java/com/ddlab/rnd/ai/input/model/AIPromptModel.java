package com.ddlab.rnd.ai.input.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AIPromptModel {
	
	@JsonProperty
	private String model;
	@JsonProperty("messages")
    private List<PromptMessageModel> messages;
	@JsonProperty("max_tokens")
    private Integer maxTokens;
    //@JsonProperty("temperature")
    //private Double temperature = 0.0; //default for more deterministic output

}
