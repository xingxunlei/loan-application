package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class User implements Serializable{
	
	private String id;
	private String nickyName;
	private String pwd;
}
