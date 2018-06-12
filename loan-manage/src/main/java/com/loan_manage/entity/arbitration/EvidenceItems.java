package com.loan_manage.entity.arbitration;

import com.loan_manage.service.impl.ArbitrationServiceImpl;
import com.loan_manage.utils.Detect;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanzezhong on 2018/3/27.
 */
@Getter
@Setter
public class EvidenceItems implements Serializable {
    /*****借款协议******/
    public String materialCode;
    /*****借款协议******/
    public String name;
    /*****该证据证明各方签订借款协议，并约定借款数额及相应的违约责任等。******/
    public String content;
    /*****true******/
    public boolean hasOriginal;
    /*****文件*****/
    public List<Files> files;
    /*****扩展字段****/
    public EvidenceItemsExtData extData;

    /**
     * 证据项-借款协议
     */
    public static EvidenceItems getEvidenceItems1(EvidenceItemsExtData extData){
        EvidenceItems evidenceItems = new EvidenceItems();
        evidenceItems.setMaterialCode("借款协议");
        evidenceItems.setName("借款协议");
        evidenceItems.setContent("该证据证明各方签订借款协议，并约定借款数额及相应的违约责任等。");
        evidenceItems.setHasOriginal(true);
        evidenceItems.setFiles(Files.getFiles(ArbitrationServiceImpl.BORROW_AGREEMENT));

        EvidenceItemsExtData extData1 = new EvidenceItemsExtData();
        extData1.setBorrowerName(extData.getBorrowerName());
        extData1.setLenderName(extData.getLenderName());
        extData1.setServiceCompany(extData.getServiceCompany());
        extData1.setLoanDate(extData.getTransactionTime());
        extData1.setLoanContractMoney(extData.getLoanContractMoney());
        extData1.setDailyRate(extData.getDailyRate());
        extData1.setLoanDays(extData.getLoanDays());
        extData1.setRepaymentDate(extData.getRepaymentDate());
        extData1.setServiceFee1(extData.getServiceFee1());
        extData1.setServiceFee2(extData.getServiceFee2());
        extData1.setServiceFeeDiscount(extData.getServiceFeeDiscount());
        extData1.setLiquidatedDamages1DailyRate(extData.getLiquidatedDamages1DailyRate());
        extData1.setLiquidatedDamages2Rate(extData.getLiquidatedDamages2Rate());
        extData1.setBorrowerServedMobile(extData.getBorrowerServedMobile());
        extData1.setBorrowerServedEmail(extData.getBorrowerServedEmail());
        extData1.setLoanContractNo(extData.getLoanContractNo());
        evidenceItems.setExtData(extData1);
        return evidenceItems;
    }

    /**
     * 证据项-支付凭证
     */
    public static EvidenceItems getEvidenceItems2(EvidenceItemsExtData extData){
        EvidenceItems evidenceItems = new EvidenceItems();
        evidenceItems.setMaterialCode("支付凭证");
        evidenceItems.setName("支付凭证");
        evidenceItems.setContent("证明借款发放的事实。");
        evidenceItems.setHasOriginal(true);
        evidenceItems.setFiles(Files.getFiles(ArbitrationServiceImpl.PAYMENT_VOUCHER));

        EvidenceItemsExtData extData1 = new EvidenceItemsExtData();
        extData1.setBorrowerName(extData.getBorrowerName());
        extData1.setTransactionTime(extData.getTransactionTime());
        extData1.setTransactionOderNo(extData.getTransactionOderNo());
        extData1.setLoanContractNo(extData.getLoanContractNo());
        extData1.setTransactionSerialNo(extData.getTransactionSerialNo());
        extData1.setLoanMoney(extData.getLoanMoney());
        extData1.setLoanAccount(extData.getLoanAccount());
        extData1.setLenderAccount(extData.getLenderAccount());
        evidenceItems.setExtData(extData1);
        return evidenceItems;
    }

    /**
     * 证据项-还款记录
     */
    public static EvidenceItems getEvidenceItems3(List<Map<String, Object>> list){
        EvidenceItems evidenceItems = new EvidenceItems();
        evidenceItems.setMaterialCode("还款记录");
        evidenceItems.setName("还款记录");
        evidenceItems.setContent("该证据证明借款人对出借人的还款记录凭证。");
        evidenceItems.setHasOriginal(true);
        evidenceItems.setFiles(Files.getFiles(ArbitrationServiceImpl.PDF_REPAY_RECORD));
        EvidenceItemsExtData extData1 = new EvidenceItemsExtData();
        if(Detect.notEmpty(list)){
            extData1.setBorrowerName(reversalSprit("customerName", list));
            extData1.setLoanContractNo(reversalSprit("borrNum", list));
            extData1.setRepayTime(reversalSprit("repayTime", list));
            extData1.setTransactionOderNo(reversalSprit("orderCode", list));
            extData1.setTransactionSerialNo(reversalSprit("orderCode", list));
            extData1.setRepayMoney(reversalSprit("repayAmount", list));
            extData1.setRepayAccount(reversalSprit("bankCardNum", list));
        }
        evidenceItems.setExtData(extData1);
        return evidenceItems;
    }

    /**
     * 证据项-催收记录
     */
    public static EvidenceItems getEvidenceItems4(List<Map<String, Object>> list){
        EvidenceItems evidenceItems = new EvidenceItems();
        evidenceItems.setMaterialCode("催收记录");
        evidenceItems.setName("催收记录");
        evidenceItems.setContent("该证据证明借款人对出借人未按时付款的催收行为，包含对借款协议中约定的电话及邮箱的催收记录。");
        evidenceItems.setHasOriginal(true);
        evidenceItems.setFiles(Files.getFiles(ArbitrationServiceImpl.PDF_COLLECTION_RECORD));
        EvidenceItemsExtData extData1 = new EvidenceItemsExtData();
        if(Detect.notEmpty(list)){
            extData1.setLoanContractNo(reversalSprit("borrNum", list));
            extData1.setDunningTime(reversalSprit("collectionTime", list));
            extData1.setDunningType(reversalSprit("dunningType", list));
            extData1.setDunningTocall(reversalSprit("dunningTocall", list));
            extData1.setDunningRemark(reversalSprit("collectionResult", list));
            extData1.setDunningPerson(reversalSprit("collectionUser", list));
        }else {
            extData1.setLoanContractNo("");
            extData1.setDunningTime("");
            extData1.setDunningType("");
            extData1.setDunningTocall("");
            extData1.setDunningRemark("");
            extData1.setDunningPerson("");
        }
        evidenceItems.setExtData(extData1);
        return evidenceItems;
    }

    /**
     * 证据项-签章授权书
     */
    public static EvidenceItems getEvidenceItems5(EvidenceItemsExtData extData){
        EvidenceItems evidenceItems = new EvidenceItems();
        evidenceItems.setMaterialCode("签章授权书");
        evidenceItems.setName("签章授权书");
        evidenceItems.setContent("该证据证明借款人授权受托人提交相关材料。");
        evidenceItems.setHasOriginal(true);
        evidenceItems.setFiles(Files.getFiles(ArbitrationServiceImpl.AUTH_ENTRUSTMENT_NAME));

        EvidenceItemsExtData extData1 = new EvidenceItemsExtData();
        extData1.setBorrowerName(extData.getBorrowerName());
        extData1.setSignPowerofAttorneyDate(extData.getSignPowerofAttorneyDate());
        evidenceItems.setExtData(extData1);
        return evidenceItems;
    }

    private static String reversalSprit(String key ,  List<Map<String, Object>> list){
        List<String> value = new ArrayList();
        for(Map<String, Object> hashMap : list){
            value.add(StringUtils.isEmpty(hashMap.get(key)) ? "0" : hashMap.get(key) + "");
        }
        return StringUtils.collectionToDelimitedString(value, ",");
    }

}
