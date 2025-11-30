package com.ddlab.rnd.ai.output.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class Message {
	
	private String role;
    private String content;
    private Object refusal;


}
