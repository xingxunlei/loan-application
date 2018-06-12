package com.loan_api.app;

import com.loan_entity.app.NoteResult;
import com.loan_entity.app.Person;
import com.loan_entity.app.PersonNotify;
import com.loan_entity.manager.Feedback;

public interface NotifyService {

	/**
	 * 注册用户推送
	 * @param personNotify 用户推送注册信息
	 * @return 返回用户推送注册请求状态
	 */
	public NoteResult registerPersonNotify(PersonNotify personNotify);

	/**
	 * 解绑用户推送
	 * @param personNotify 用户推送注册信息
	 * @return 返回用户推送解绑请求状态
	 */
	public NoteResult unregisterPersonNotify(PersonNotify personNotify);
}