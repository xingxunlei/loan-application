package com.loan_api.loan;

import com.loan_entity.app.NoteResult;
import com.loan_entity.loan_vo.BatchCollectEntity;
import com.loan_entity.utils.Callback;

import java.util.List;


public interface YsbCollectionService {

    /**
     * 发起代扣请求
     * @param borrNum
     * @param name
     * @param idCardNo
     * @param optAmount
     * @param bankId
     * @param bankNum
     * @param phone
     * @param description
     * @return
     */
    public NoteResult askCollection(String guid,String borrNum,String name,String idCardNo,
            String optAmount,String bankId,String bankNum,
            String phone,String description,String serNo,String createUser, String collectionUser);

    /**
     * 处理第三方回调
     * @param callback
     * @return
     */
    public NoteResult collectCallback(Callback callback);
    
    /**
     * 定时任务，查询处理中的订单
     */
    public void queryCollectStatus();
    
    /**
     * .net单独查询订单状态接口
     */
    public NoteResult netQueryOrder(String serNo);

    public NoteResult askCollectionBatch(List<BatchCollectEntity> param);

    public String testCallback(String orderId);

    public NoteResult orderStatus(String serialNo);


   }
