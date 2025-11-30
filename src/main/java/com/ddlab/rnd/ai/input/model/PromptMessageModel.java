package com.ddlab.rnd.ai.input.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PromptMessageModel {

	@JsonProperty
	private String role;

	@JsonProperty
    private String content;
}
