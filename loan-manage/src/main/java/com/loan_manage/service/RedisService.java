
package com.loan_manage.service;

import com.alibaba.fastjson.JSONObject;
import com.loan_entity.app.Bank;
import com.loan_entity.manager.BankList;

import java.util.List;
import java.util.Map;

public interface RedisService {

    /**
     * 从redis里取出产品信息
     * @param productId 产品ID
     * @return {id:1,name:'1000-14天'}
     */
    JSONObject selectProductFromRedis(Integer productId);

    /**
     * 根据用户ID从redis中取出用户
     * @param customerId 用户ID
     * @return {name:'张三',idCard:'1234567890',phone:'1365689745'}
     */
    JSONObject selectPersonFromRedis(Integer customerId);

    /**
     * 根据银行卡ID获取银行卡信息
     * @param bankId
     * @return {id:1,bankId:1,bankName=xxx,bankNum=123}
     */
    JSONObject selectBankInfoFromRedis(String bankId);


    /**
     * 根据用户ID查询用户银行卡信息
     * @param customerId 用户ID
     * @return {id:"132",bankName:" 中国银行",careatDate:"2017-07-03",phone:"13636569843" bankNum:"1365689745",score:9999|0000}
     */
    List<Bank> selectAllBankFromRedis(Integer customerId);

    /**
     * 根据用户ID查询查询主卡
     * @param customerId
     * @return
     */
    JSONObject selectMainBankFromRedis(Integer customerId);
    
    /**
     * Map参数组装
     * @param result
     * @param param
     */
    void getDataByRedis(List<Map<String, Object>> result, Map<String, String> param);

    /**
     * 根据号码查询用户ID
     * @param phone
     * @return
     */
	JSONObject selectPhoneFromRedis(String phone);

    /**
     * Map查询参数组装
     * @param param
     */
    Map<String, Object> getSerchParam(Map<String, Object> param);


	/**
	 * 根据身份证号查用户Id
	 * @param cardNum
	 * @return
	 */
	JSONObject selectCardNumFromRedis(String cardNum);
	
	/**
	 * 根据银行卡号查用户Id
	 * @param bankNum
	 * @return
	 */
	JSONObject selectBankNumFromRedis(String bankNum);

    /**
     * 根据合同号获取催收信息
     * @param contractID
     * @return
     */
    JSONObject selectCollertorsFromRedis(String contractID);

    /**
     * 更新redis内容
     * @param contractId
     */
    void updateCollertorsFromRedis(String contractId,String userId,String username,String updateDate);

    JSONObject selectCollertorsUserName(String userId);

    /**
     * 更新还款redis
     * @param contractId 合同号
     * @param amount  累计还款总额（累加）
     * @param repayTime 最新操作时间（可直接覆盖）
     */
    void updateRepaymentSum(String contractId,String amount,String repayTime);

    /**
     * 获得扣款信息
     * @param contractId 合同号
     * @return
     */
    JSONObject selectRepaymentSum(String contractId);

    /**
     * 同步登录用户
     * @return
     */
    boolean syanLoginUser();

    /**
     * 用户登录接口
     * @param userName
     * @param pwd
     */
    String getLoginUser(String userName, String pwd);

    /**
     * 获取Redis
     * @param key
     * @return
     */
    String getRedistByKey(String key);

    /**
     * 获取贷后无参总条数
     * @param args
     * @return
     */
    long selectQueryTotalItem(String type,String args);

//    /**
//     * 获取贷后有参总条数
//     * @param type
//     * @param condition
//     * @param permission 可空
//     * @return
//     */
//    long selectQueryTotalItemWithCAndP(String type,String condition,String permission);

    /**
     * 保存有参
     * @param totalCount
     */
    void saveQueryTotalItem(String type, String keySuffix,long totalCount);

//    /**
//     * 保存有参(条件,权限)
//     * @param type
//     * @param condition
//     * @param permission
//     */
//    void saveQueryTotalItemWithCAndP(String type, String condition,String permission,long totalCount);

    /**
     * 保存批量扣款标记
     * @param tagName
     * @throws Exception
     */
    void saveBatchCollectionTags(String tagName);

    /**
     * 获取批量扣款
     * @param key
     * @return
     */
    String selectBatchCollectionTags(String key);

    /**
     *  获取银行列表
     * @return
     */
    List<BankList> getBankList();

    /**
     * 保存当前下载数
     * @param count
     */
    void saveDownloadCount(int count);

    /**
     * 查询当前下载数
     * @return
     */
    int selectDownloadCount();

    /**
     * 检查扣款锁
     * @param integer
     * @return
     */
    boolean checkLock(Integer integer);

    /**
     * 检查线下还款、申请减免锁
     * @param contractId
     * @return
     */
    boolean selectCanReduce(String contractId);

}
