package com.loan_api.app;


import com.loan_entity.app.User;


public interface UserApi {

	
	//根据id查询用户信息
	public User findUserById(int id) ;
	
	

}
