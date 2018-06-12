
package com.loan_api.contract;

import com.loan_entity.manager.*;
import com.loan_entity.manager_vo.CouponVo;
import com.loan_entity.manager_vo.FeedbackVo;
import com.loan_entity.manager_vo.MsgTemplateVo;
import com.loan_entity.manager_vo.QuestionVo;
import com.loan_entity.utils.ManagerResult;

import java.util.List;

/**
 *描述：电子合同api
 *@author: carl.wan
 *@date： 日期：2017年11月23日 10:33:00
 */
public interface ElectronicContractService {

    /**
     * 生成合同
     * @param borrId
     * @return
     */
    String createElectronicContract(Integer borrId);
    String createElectronicContract(Integer borrId, String productId);
    /**
     * 查询预览合同
     * @param borrId
     * @return
     */
    String queryElectronicContract(Integer borrId);

    /**
     * 查询正式合同
     * @param borrNum
     * @return
     */
    String downElectronicContract(String borrNum);
    String downElectronicContract(String borrNum, String productId);

    /**
     * 回调
     * @param borrNum
     * @return
     */
    String callBack(String code,String url,String borrNum);
    String callBack(String code,String url,String borrNum, String productId);

    /**
     * 处理异常电子合同数据
     * @return
     */
    void disposeExceptionContract();

}
