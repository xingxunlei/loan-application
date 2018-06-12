package com.loan_utils.entity;

import java.io.Serializable;

public class BQSEntity implements Serializable {
  // <summary>
    // 事件类型
    // </summary>
    private String eventType ;

    // <summary>
    // 当前会话标识
    // </summary>
    private String tokenKey ;

    // <summary>
    // 事件发生时间
    // </summary>
    private String occurTime ;

    // <summary>
    // 应用平台类型
    // </summary>
    private String platform ;
    
    // <summary>
    // 用户账号
    // </summary>
    private String account ;

    // <summary>
    // 用户姓名
    // </summary>
    private String name ;

    // <summary>
    // 用户邮箱
    // </summary>
    private String mail ;

    // <summary>
    // 用户手机号
    // </summary>
    private String phone ;

    // <summary>
    // 用户身份证
    // </summary>
    private String idValue ;

    // <summary>
    // 用户住址
    // </summary>
    private String address ;

    // <summary>
    // 用户所在城市
    // </summary>
    private String addressCity ;

    // <summary>
    // 用户联系人姓名
    // </summary>
    private String contactsName ;

    // <summary>
    // 用户联系人电话
    // </summary>
    private String contactsMobile ;

    // <summary>
    // 用户工作单位名称
    // </summary>
    private String organization ;

    // <summary>
    // 用户工作单位地址
    // </summary>
    private String organizationAddress ;

    // <summary>
    //  工作单位电话
    // </summary>
    private String organizationPhone ;
    
    // <summary>
    //  学历
    // </summary>
    private String education ;

    // <summary>
    // 毕业院校名称
    // </summary>
    private String graduateSchool ;

    // <summary>
    // 毕业院校城市
    // </summary>
    private String graduateCity ;

    // <summary>
    // 是否已婚
    // </summary>
    private String marriage ;

    // <summary>
    // 户籍所在地
    // </summary>
    private String residence ;

    // <summary>
    // BIN卡号
    // </summary>
    private String userBankBin ;

    // <summary>
    // 银行卡卡号
    // </summary>
    private String bankCardNo ;

    // <summary>
    // 银行卡类型
    // </summary>
    private String bankCardType ;

    // <summary>
    // 银行户名
    // </summary>
    private String bankCardName ;

    // <summary>
    // 银行卡预留号码
    // </summary>
    private String bankCardMobile ;

    // <summary>
    // 信用卡卡号
    // </summary>
    private String creditCardNo ;

    // <summary>
    // 信用卡户名
    // </summary>
    private String creditCardName ;

    // <summary>
    // 信用卡预留手机号
    // </summary>
    private String creditCardMobile ;

    // <summary>
    // 浏览器UA
    // </summary>
    private String userAgentCust ;

    // <summary>
    // REFFER
    // </summary>
    private String referCust ;

    // <summary>
    // IP地址
    // </summary>
    private String ip ;

    // <summary>
    // MAC地址
    // </summary>
    private String mac ;

    // <summary>
    // IMEI
    // </summary>
    private String imei ;

    // <summary>
    // 经度
    // </summary>
    private String longitude ;

    // <summary>
    // 纬度
    // </summary>
    private String latitude ;

    // <summary>
    // 客户业务处理结果
    // </summary>
    private String bizResult ;

    // <summary>
    // 金额
    // </summary>
    private double amount ;

    // <summary>
    // 卖家或收款人账号
    // </summary>
    private String payeeAccount ;

    // <summary>
    // 卖家或收款人姓名
    // </summary>
    private String payeeName ;

    // <summary>
    // 卖家或收款人邮箱
    // </summary>
    private String payeeEmail ;

    // <summary>
    // 卖家或收款人手机
    // </summary>
    private String payeeMobile ;

    // <summary>
    // 卖家或收款人座机
    // </summary>
    private String payeePhone ;

    // <summary>
    // 卖家或收款人身份证
    // </summary>
    private String payeeIdNumber ;

    // <summary>
    // 卖家或收款人银行卡号
    // </summary>
    private String payeeCardNumber ;

    // <summary>
    // 商户名称
    // </summary>
    private String merchantName ;

    // <summary>
    // 商户工商注册号
    // </summary>
    private String bizLicense ;

    // <summary>
    // 组织机构代码
    // </summary>
    private String orgCode ;

    // <summary>
    // 组织机构代码
    // </summary>
    private String payMethod ;

    // <summary>
    // 支付金额

    // </summary>
    private String payAmount;

    // <summary>
    // 支付卡号

    // </summary>
    private String payAccount;
    
    /// <summary>
    /// 用户联系人姓名2
    /// </summary>
    private String contactsNameSec ;

    /// <summary>
    /// 用户联系人电话2
    /// </summary>
    private String contactsMobileSec ;

    /// <summary>
    /// 子女数
    /// </summary>
    private String loc_childrenNum ;

    /// <summary>
    /// 职业
    /// </summary>
    private String loc_occupation ;

    /// <summary>
    /// 月收入
    /// </summary>
    private String loc_income ;

    
    public String getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(String occurTime) {
        this.occurTime = occurTime;
    }

    public String getContactsNameSec() {
        return contactsNameSec;
    }

    public void setContactsNameSec(String contactsNameSec) {
        this.contactsNameSec = contactsNameSec;
    }

    public String getContactsMobileSec() {
        return contactsMobileSec;
    }

    public void setContactsMobileSec(String contactsMobileSec) {
        this.contactsMobileSec = contactsMobileSec;
    }

    public String getLoc_childrenNum() {
        return loc_childrenNum;
    }

    public void setLoc_childrenNum(String loc_childrenNum) {
        this.loc_childrenNum = loc_childrenNum;
    }

    public String getLoc_occupation() {
        return loc_occupation;
    }

    public void setLoc_occupation(String loc_occupation) {
        this.loc_occupation = loc_occupation;
    }

    public String getLoc_income() {
        return loc_income;
    }

    public void setLoc_income(String loc_income) {
        this.loc_income = loc_income;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public String getOccyrTime() {
        return occurTime;
    }

    public void setOccyrTime(String occurTime) {
        this.occurTime = occurTime;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

   
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdValue() {
        return idValue;
    }

    public void setIdValue(String idValue) {
        this.idValue = idValue;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getContactsName() {
        return contactsName;
    }

    public void setContactsName(String contactsName) {
        this.contactsName = contactsName;
    }

    public String getContactsMobile() {
        return contactsMobile;
    }

    public void setContactsMobile(String contactsMobile) {
        this.contactsMobile = contactsMobile;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganizationAddress() {
        return organizationAddress;
    }

    public void setOrganizationAddress(String organizationAddress) {
        this.organizationAddress = organizationAddress;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getGraduateSchool() {
        return graduateSchool;
    }

    public void setGraduateSchool(String graduateSchool) {
        this.graduateSchool = graduateSchool;
    }

    public String getGraduateCity() {
        return graduateCity;
    }

    public void setGraduateCity(String graduateCity) {
        this.graduateCity = graduateCity;
    }

    public String getMarriage() {
        return marriage;
    }

    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getUserBankBin() {
        return userBankBin;
    }

    public void setUserBankBin(String userBankBin) {
        this.userBankBin = userBankBin;
    }

    public String getBankCardNo() {
        return bankCardNo;
    }

    public void setBankCardNo(String bankCardNo) {
        this.bankCardNo = bankCardNo;
    }

    public String getBankCardType() {
        return bankCardType;
    }

    public void setBankCardType(String bankCardType) {
        this.bankCardType = bankCardType;
    }

    public String getBankCardName() {
        return bankCardName;
    }

    public void setBankCardName(String bankCardName) {
        this.bankCardName = bankCardName;
    }

    public String getBankCardMobile() {
        return bankCardMobile;
    }

    public void setBankCardMobile(String bankCardMobile) {
        this.bankCardMobile = bankCardMobile;
    }

    public String getCreditCardNo() {
        return creditCardNo;
    }

    public void setCreditCardNo(String creditCardNo) {
        this.creditCardNo = creditCardNo;
    }

    public String getCreditCardName() {
        return creditCardName;
    }

    public void setCreditCardName(String creditCardName) {
        this.creditCardName = creditCardName;
    }

    public String getCreditCardMobile() {
        return creditCardMobile;
    }

    public void setCreditCardMobile(String creditCardMobile) {
        this.creditCardMobile = creditCardMobile;
    }

    public String getUserAgentCust() {
        return userAgentCust;
    }

    public void setUserAgentCust(String userAgentCust) {
        this.userAgentCust = userAgentCust;
    }

    public String getReferCust() {
        return referCust;
    }

    public void setReferCust(String referCust) {
        this.referCust = referCust;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getBizResult() {
        return bizResult;
    }

    public void setBizResult(String bizResult) {
        this.bizResult = bizResult;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayeeAccount() {
        return payeeAccount;
    }

    public void setPayeeAccount(String payeeAccount) {
        this.payeeAccount = payeeAccount;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getPayeeEmail() {
        return payeeEmail;
    }

    public void setPayeeEmail(String payeeEmail) {
        this.payeeEmail = payeeEmail;
    }

    public String getPayeeMobile() {
        return payeeMobile;
    }

    public void setPayeeMobile(String payeeMobile) {
        this.payeeMobile = payeeMobile;
    }

    public String getPayeePhone() {
        return payeePhone;
    }

    public void setPayeePhone(String payeePhone) {
        this.payeePhone = payeePhone;
    }

    public String getPayeeIdNumber() {
        return payeeIdNumber;
    }

    public void setPayeeIdNumber(String payeeIdNumber) {
        this.payeeIdNumber = payeeIdNumber;
    }

    public String getPayeeCardNumber() {
        return payeeCardNumber;
    }

    public void setPayeeCardNumber(String payeeCardNumber) {
        this.payeeCardNumber = payeeCardNumber;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getBizLicense() {
        return bizLicense;
    }

    public void setBizLicense(String bizLicense) {
        this.bizLicense = bizLicense;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(String payAccount) {
        this.payAccount = payAccount;
    }

    public String getOrganizationPhone() {
        return organizationPhone;
    }

    public void setOrganizationPhone(String organizationPhone) {
        this.organizationPhone = organizationPhone;
    }
    
    
}
