package com.ddlab.rnd.snyk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SnykOrg {
	
	@JsonProperty("name")
	private String name;
		
	@JsonProperty("group")
	private Group group;
	
}
