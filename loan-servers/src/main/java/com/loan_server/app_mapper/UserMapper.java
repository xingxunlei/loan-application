package com.loan_server.app_mapper;

import com.loan_entity.app.User;



public interface UserMapper {
	
	
	//根据id查询用户信息
	public User findUserById(int id) throws Exception;
	
	
	public User findUserByName(String name) throws Exception;
}
