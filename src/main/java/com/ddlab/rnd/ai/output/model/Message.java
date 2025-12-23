package com.ddlab.rnd.ai.output.model;

import lombok.Data;

@Data
public class Message {
	
	private String role;
    private String content;
    private Object refusal;


}
