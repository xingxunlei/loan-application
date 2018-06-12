package com.loan_manage.web_service;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(targetNamespace = "http://WebService.oaData.com/")
public interface PersonServiceDao {

	/**
	 * oa登陆功能
	 * 
	 * @param ifUser
	 * @param ifPwd
	 * @param username
	 * @param userpwd
	 * @return
	 */
	public String login(@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "username") String username,
			@WebParam(name = "userpwd") String userpwd);

	/**
	 * oa用户名校验
	 * 
	 * @param ifUser
	 * @param ifPwd
	 * @param username
	 * @return
	 */
	public String checklogin_id(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "username") String username);

	/**
	 * oa增加人员基本信息
	 * 
	 * @param ifUser
	 * @param ifPwd
	 * @param params
	 *            json类型的参数
	 * @return
	 */
	public String addOrUpdatePerson(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "params") String params,
			@WebParam(name = "loginid") int loginid);

	/**
	 * 删除人员
	 * 
	 * @param ifUser
	 * @param ifPwd
	 * @param deletePersons
	 * @return
	 */
	public String deletePerson(@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "deletePersons") String deletePersons);

	/********** 任职信息查询，处理assgnment ***************/

	/**
	 * 
	 * @param ifUser
	 * @param ifPwd
	 * @param params
	 *            任职信息 json字段
	 * @return
	 */
	public String addOrUpdateAssgnment(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "params") String params,
			@WebParam(name = "loginid") int loginid);

	/**
	 * 删除工作经历
	 * 
	 * @param ifUser
	 * @param ifPwd
	 * @param deleteAssgnments
	 * @return
	 */
	public String deteteAssgnment(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "deleteAssgnments") String deleteAssgnments);

	/**
	 * 查询某个人的任职经历
	 * 
	 * @param ifUser
	 * @param ifPwd
	 * @param per_id
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public String findAssgnByPer_id(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "person_id") int per_id,
			@WebParam(name = "page") int page,
			@WebParam(name = "pageSize") int pageSize);

	/**
	 * 根据条件查询人员
	 * 
	 * @param ifUser
	 * @param ifPwd
	 * @param params
	 *            请求参数
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public String findPerson(@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "params") String params,
			@WebParam(name = "page") int page,
			@WebParam(name = "pageSize") int pageSize);
	public String findPersonByLeader1(@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "params") String params,
			@WebParam(name = "page") int page,
			@WebParam(name = "pageSize") int pageSize);

	public String addOrUpdateContract(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "params") String params,
			@WebParam(name = "loginid") int loginid);

	public String deleteContract(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "contractIds") String contractIds);

	public String findContractById(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "contractId") int id);

	public String findContractByPer(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "ifPwd") String ifPwd,
			@WebParam(name = "person_id") int person_id,
			@WebParam(name = "page") int page,
			@WebParam(name = "pageSize") int pageSize);

	public String findPersonForId(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "ifPwd") String ifPwd,
			@WebParam(name = "id") int id);

	public String findAssgnById(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "id") int id);

	public String addOrUpdateBank(String ifUser, String ifPwd, String params,
			int loginid);

	public String deleteBank(String ifUser, String ifPwd, String bankIds);

	public String findBankForId(String ifUser, String ifPwd, int id);

	public String findBankForPer_id(String ifUser, String ifPwd, int person_id,
			int page, int pageSize);

	public String addOrUpdateEdu(String ifUser, String ifPwd, String params,
			int loginid);

	public String deleteEdu(String ifUser, String ifPwd, String EduIds);

	public String findEduForId(String ifUser, String ifPwd, int id);

	public String findEduForPer_id(String ifUser, String ifPwd, int person_id,
			int page, int pageSize);

	public String addOrUpdateCertifi(String ifUser, String ifPwd,
			String params, int loginid);

	public String deleteCertifi(String ifUser, String ifPwd, String contractIds);

	public String findCertifiForId(String ifUser, String ifPwd, int id);

	public String findCertifiForPer_id(String ifUser, String ifPwd,
			int person_id, int page, int pageSize);

	public String quitJob(String ifUser, String ifPwd, int assignment_id,
			String quitTime, int loginid);

	/**** 门店人事查询 *****/
	public String findPersonByStore(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "params") String params,
			@WebParam(name = "page") int page,
			@WebParam(name = "pageSize") int pageSize,
			@WebParam(name = "loginid") int loginid);

	/**** 领导查询 *****/
	public String findPersonByLeader(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "params") String params,
			@WebParam(name = "page") int page,
			@WebParam(name = "pageSize") int pageSize,
			@WebParam(name = "loginid") int loginid);

	/**** 中心人事查询 ****/
	public String findPersonByDept(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "params") String params,
			@WebParam(name = "page") int page,
			@WebParam(name = "pageSize") int pageSize,
			@WebParam(name = "loginid") int loginid);

	/***** 查询用户 ******/
	public String findUser(@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "params") String params,
			@WebParam(name = "page") int page,
			@WebParam(name = "pageSize") int pageSize,
			@WebParam(name = "loginid") int loginid);

	/***** 更新账户有效性 ******/
	public String updateUserStatus(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "id") int id,
			@WebParam(name = "status_code") String status_code,
			@WebParam(name = "loginid") int loginid);

	/***** 更新密码 ******/
	public String updateUserPwd(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "person_id") int person_id,
			@WebParam(name = "old_pwd") String old_pwd,
			@WebParam(name = "new_pwd") String new_pwd,
			@WebParam(name = "loginid") int loginid);

	/****** 中心人事批量更新 **********/
	public String batchUpdateStatus(
			@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "updateStr") String updateStr,
			@WebParam(name = "loginid") int loginid);

	/********* 门店人事申请离职 ***********/
	public String applyQuitJob(@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd,
			@WebParam(name = "assignment_id") int assignment_id,
			@WebParam(name = "quitTime") String quitTime,
			@WebParam(name = "loginid") int loginid);
	
	/***** findPerson返回总数******/ 
	public String countfindPerson(@WebParam(name="interfaceUser")String ifUser,@WebParam(name="interfacePwd")String ifPwd,@WebParam(name="params")String params,@WebParam(name="page")int page,@WebParam(name="pageSize")int pageSize);
	
	
	//修改人员更新
	public String ExcelInfoUpdate(@WebParam(name = "filePath") String filePath,
			@WebParam(name = "loginid") int loginid);
	
	/***** 合同批量导入（导入excel） ******/
	public String ExcelHeTongAdd(@WebParam(name = "filePath") String filePath,
			@WebParam(name = "loginid") int loginid);
	
	/***** 同步OA用户  ******/
	public String selectAllUser(@WebParam(name = "interfaceUser") String ifUser,
			@WebParam(name = "interfacePwd") String ifPwd);
	
}
