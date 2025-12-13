package com.ddlab.rnd.snyk.project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectIdDatum {

	@JsonProperty("type")
	private String type;
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("attributes")
	private ProjectIdAttributes attributes;
}
