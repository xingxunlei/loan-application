package com.loan_manage.service;


import com.github.pagehelper.PageInfo;
import com.loan_entity.loan.CollectorsLevel;
import com.loan_entity.manager.LoanCompany;
import com.loan_manage.entity.Result;

import java.util.List;
import java.util.Map;

public interface CollectorsLevelService {
    /**
     * 查询系统用户
     * @param queryMap
     * @param offset
     * @param size
     * @param userNo
     * @return
     */
    PageInfo<CollectorsLevel> selectCollectorsLevels(Map<String, Object> queryMap, int offset, int size, String userNo);

    /**
     * 生成不重复的工号
     * @return
     */
    String generateNewSysno();

    /**
     * 操作系统用户信息
     * @param collectorsLevel
     * @return
     */
    int editCollectorsLevel(CollectorsLevel collectorsLevel);

    /**
     * 获取登录用户
     * @param userName
     * @param password
     * @return
     */
    Result loadLoginUser(String userName,String password,Integer source,String loginVerifyCode);

    /**
     * 编辑公司信息
     * @param company
     * @return
     */
    int editCompanyInfo(LoanCompany company);

    /**
     * 发送验证码
     * @param userName 用户名
     * @param password 密码
     * @return
     */
    Result sendVerifyCode(String userName, String password);

    /**
     * 校验登录名
     * @param loginname
     * @return
     */
    Result checkLoginName(String loginname);

    /**
     * 是否为外包
     * @param userSysNo
     * @return
     */
    Boolean isOutWorker(String userSysNo);

    /**
     * 修改密码
     * @param userNo
     * @param oldPwd
     * @param newPwd
     * @param newPwdConfirm
     * @return
     */
    Result modifyPassword(String userNo, String oldPwd, String newPwd, String newPwdConfirm);

    /**
     * 查询风控人员
     * @return
     */
    List<CollectorsLevel> selectCollectorsLevels();

    /**
     * 获取权限信息
     * @param userAuth
     * @return
     */
    Result loadUserAuthInfo(String userAuth);

    /**
     * 是否为可用的管理员
     * @param userName
     * @return
     */
    boolean isManager(String userName);

    /**
     * 加载人员权限信息
     * @return
     * @param category
     */
    Result loadUserRoleInfo(String category);

    /**
     * 获取用户权限信息
     * @param userName
     * @return
     */
    String getUserAuth(String userName);
}
