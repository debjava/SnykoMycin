package com.ddlab.rnd.ai.output.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Usage {

	@JsonProperty("prompt_tokens")
	private int promptTokens;
	@JsonProperty("total_tokens")
    private int totalTokens;
    @JsonProperty("completion_tokens")
    private int completionTokens;
}
