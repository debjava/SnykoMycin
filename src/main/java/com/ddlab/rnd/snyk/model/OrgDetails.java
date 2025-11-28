package com.ddlab.rnd.snyk.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrgDetails {

	@JsonProperty("orgs")
	private List<SnykOrg> orgs;
}
