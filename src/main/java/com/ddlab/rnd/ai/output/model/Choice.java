package com.ddlab.rnd.ai.output.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Choice {
	
	@JsonProperty
	private int index;
	@JsonProperty
    private Message message;
}
