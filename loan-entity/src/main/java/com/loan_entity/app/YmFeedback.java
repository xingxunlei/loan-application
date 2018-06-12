package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class YmFeedback implements Serializable{
	
	private int id;
	private int per_id;
	private String content;
	private String create_time;
}
