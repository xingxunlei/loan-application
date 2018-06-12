package com.jhh.model;

import java.io.Serializable;

public class BorrowingExpertsPerson implements Serializable{

	private static final long serialVersionUID = 7027674526144297373L;
	
	private String user_mobile;
	
	private String register_time;

	public String getUser_mobile() {
		return user_mobile;
	}

	public void setUser_mobile(String user_mobile) {
		this.user_mobile = user_mobile;
	}

	public String getRegister_time() {
		return register_time;
	}

	public void setRegister_time(String register_time) {
		this.register_time = register_time;
	}
}
