package com.loan_manage.entity;

import com.loan_entity.enums.OrderRLStatusEnum;
import com.loan_entity.enums.OrderTypeEnum;
import com.loan_entity.enums.SpecialUserEnum;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * 还款流水导出
 */
public class DownloadOrder implements Serializable {
    @Excel(name = "合同编号",width = 20)
    private String borrNum;
    @Excel(name = "还款流水",width = 20)
    private String serialNo;
    @Excel(name = "姓名")
    private String name;
    @Excel(name = "身份证号",width = 20)
    private String idCard;
    @Excel(name = "手机号")
    private String phone;
    @Excel(name = "开户行")
    private String bankName;
    @Excel(name = "银行卡号",width = 20)
    private String bankNum;
    @Excel(name = "还款类型")
    private String typeName;
    @Excel(name = "还款状态")
    private String state;
    @Excel(name = "还款金额")
    private String actAmount;
    @Excel(name = "还款时间",width = 20)
    private String createDate;
    @Excel(name = "操作人")
    private String username;
    @Excel(name = "催收人")
    private String bedueUser;
    @Excel(name = "备注",width = 40)
    private String reason;
    private String borrId;
    private String prodId;
    private String id;
    private String bankId;
    private String perId;
    private String rlState;
    private String type;
    private String bedueDays;
    private String createUser;
    private String collectionUser;
    private String deductionsType;

    public String getBorrNum() {
        return borrNum;
    }

    public void setBorrNum(String borrNum) {
        this.borrNum = borrNum;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBankNum() {
        return bankNum;
    }

    public void setBankNum(String bankNum) {
        this.bankNum = bankNum;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getActAmount() {
        return actAmount;
    }

    public void setActAmount(String actAmount) {
        this.actAmount = actAmount;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBedueUser() {
        return bedueUser;
    }

    public void setBedueUser(String bedueUser) {
        this.bedueUser = bedueUser;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getBorrId() {
        return borrId;
    }

    public void setBorrId(String borrId) {
        this.borrId = borrId;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getPerId() {
        return perId;
    }

    public void setPerId(String perId) {
        this.perId = perId;
    }

    public String getRlState() {
        return rlState;
    }

    public void setRlState(String rlState) {
        this.rlState = rlState;
        this.state = OrderRLStatusEnum.getDescByCode(rlState);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        this.typeName = OrderTypeEnum.getDescByCode(type);
    }

    public String getBedueDays() {
        return bedueDays;
    }

    public void setBedueDays(String bedueDays) {
        this.bedueDays = bedueDays;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
        String desc = SpecialUserEnum.getDescByCode(createUser);
        if(StringUtils.isNotBlank(desc)){
            this.username = desc;
        }
    }

    public String getCollectionUser() {
        return collectionUser;
    }

    public void setCollectionUser(String collectionUser) {
        this.collectionUser = collectionUser;
        String desc = SpecialUserEnum.getDescByCode(createUser);
        if(StringUtils.isNotBlank(desc)){
            this.bedueUser = desc;
        }
    }

    public String getDeductionsType() {
        return deductionsType;
    }

    public void setDeductionsType(String deductionsType) {
        this.deductionsType = deductionsType;
    }


    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
