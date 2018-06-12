package com.loan_server.app_service;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.loan_api.app.UserApi;
import com.loan_entity.app.User;
import com.loan_server.app_mapper.UserMapper;

/**
 * 
 * <p>
 * Title: UserDaoImpl
 * </p>
 * <p>
 * Description:dao接口实现类
 * </p>
 * <p>
 * Company: www.itcast.com
 * </p>
 * 
 * 
 */
@Service
public class UserApiImpl implements UserApi {
	@Autowired
	private UserMapper userMapper;
	public User findUserById(int id) {
		User user = null;
		try {
			user = userMapper.findUserById(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}

}
