package com.ddlab.rnd.ai.output.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LLMModelData {
	
	@JsonProperty("model")
	private String model;
	@JsonProperty("type")
	private List<String> type;
	@JsonProperty("max_model_length")
	private String maxModelLength;
	@JsonProperty("object")
	private String object;
	@JsonProperty("owned_by")
	private String ownedBy;

}
