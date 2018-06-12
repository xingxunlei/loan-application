package com.loan_manage.service;

import com.alibaba.dubbo.remoting.TimeoutException;
import com.github.pagehelper.PageInfo;
import com.loan_entity.app.BankVo;
import com.loan_entity.app.Product;
import com.loan_entity.loan.CollectorsLevel;
import com.loan_entity.loan.ReceiptUsers;
import com.loan_entity.manager.BankList;
import com.loan_entity.manager.CollectorsListVo;
import com.loan_entity.manager.CollectorsRemark;
import com.loan_entity.manager.Download;
import com.loan_entity.manager_vo.LoanInfoVo;
import com.loan_entity.manager_vo.LoanManagementVo;
import com.loan_manage.entity.AskCollection;
import com.loan_manage.entity.LoansRemarkOutVo;
import com.loan_manage.entity.LoansRemarkVo;
import com.loan_manage.entity.Result;
import com.loan_manage.exception.RedisException;

import java.util.List;
import java.util.Map;

public interface LoanManagementService {
    /**
     * 分页加载贷后管理
     * @param queryMap 查询参数
     * @return
     */
    //PageInfo<LoanManagementVo> selectLoanManagementInfo(Map<String, Object> queryMap);
    PageInfo<LoanManagementVo> selectLoanManagementInfo(Map<String, Object> queryMap,Integer start,Integer size,String userNo);

    PageInfo<LoanManagementVo> selectBatchReduceInfo(Map<String, Object> queryMap,Integer start,Integer size,String userNo);

    /**
     * 分页加载催收人员
     * @param queryMap
     * @return
     */
    PageInfo<ReceiptUsers> selectReceiptUsers(Map<String, Object> queryMap,int offset,int size);

    /**
     * 申请减免
     * @param contractId 合同号
     * @param reduce 减免金额
     * @return
     */
    Result reduceLoan(String contractId, String reduce, String remark,String type, String userName);

    /**
     * 转件
     * @param contractIds 合同号
     * @param userId 转至人ID
     * @param opUserId 操作人ID
     * @return
     */
    Map<String,Object> transferLoan(String contractIds, String userId,String opUserId);

    /**
     *黑名单
     *//*
    int blackList(Integer conId,String userId,String reason);*/

    /**
     * 分页查询催收队列
     * @param queryMap 查询参数
     * @return
     */
    PageInfo<CollectorsListVo> selectCollectorsListVo(Map<String, Object> queryMap);

    /**
     * 添加催收备注
     * @param remark
     * @return
     */
    int addCollectionRemark(CollectorsRemark remark);

    /**
     * 查询用户主卡
     * @param userId
     * @return
     */
    BankVo selectMainBankByUserId(Integer userId);

    /**
     * 请求扣款
     * @param askCollection
     * @return
     */
    Result askCollection(AskCollection askCollection);

    /**
     * 查询所有银行列表
     * @return
     */
    List<BankList> selectBankList();

    /**
     * 查询催收信息
     * @param queryMap
     * @return
     */
    PageInfo<CollectorsListVo> selectCollectorsInfo(Map<String, Object> queryMap);
    PageInfo<CollectorsListVo> selectCollectorsInfo(Map<String, Object> queryMap, int offset, int size, String userNo);
    /**
     * 查询导出条数
     * @param queryMap
     * @return
     */
    int queryExportCount(Map<String, Object> queryMap);

    /**
     * 查询导出信息
     * @param queryMap
     * @return
     */
    List<LoanManagementVo> selectExportData(Map<String, Object> queryMap,Integer count,String userNo);

    /**
     * 查询备注导出
     * @param queryMap
     * @return
     */
    List<LoansRemarkVo> selectExportLoansRemarkVo(Map<String, Object> queryMap,String userNo);

    /**
     * 查询催收备注导出,给外包
     * @param queryMap
     * @param userNo
     * @return
     */
    List<LoansRemarkOutVo> selectExportLoansRemarkForOutWorkers(Map<String,Object> queryMap,String userNo);
    /**
     * 查询导出条数
     * @param queryMap
     * @return
     */
    int queryExportRemarkCount(Map<String, Object> queryMap,String userNo);

    /**
     * 批量代扣
     * @param askCollections
     * @param reduceMoney
     * @return
     */
    Result batchCollection(
            List<LoanManagementVo> askCollections,
            String reduceMoney,
            String createUser,
            String deductionsType,
            String collectType
    ) throws TimeoutException;

    /**
     * 洗白
     * @return
     */
    int whiteBlackList(Integer conId,String userId,String reason,String type);

    /**
     * 修改用户黑名单
     * @return
     */
    List<String> modifyPersonBlackList();

    /**
     * 拉卡拉批量代扣
     * @return
     */
    Result lakalaBatchCollection(List<LoanManagementVo> askCollections, String reduceMoney, String createUser, String deductionsType);

    /**
     * 拉卡拉代扣
     * @param askCollection
     * @return
     */
    Result lakalaAskCollection(AskCollection askCollection);

    /**
     * 检查是否可以下载及检查最大下载条数
     * @return
     */
    Download checkCanDownload();

    /**
     * 查询所有产品
     * @return
     */
    List<Product> selectProducts();


    List<LoanInfoVo>  selectLoanInfoPrivateVo(int perId);

    /**
     * 拉取催收人员
     * @param userNo
     * @return
     */
    //PageInfo<CollectorsLevel> selectReceiptUsers(String userNo,int offset,int size);

    PageInfo<CollectorsLevel> selectReceiptUsers(Map<String, Object> queryParams, String userNo, Integer type, int offset, int size);
}
