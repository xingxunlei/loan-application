package com.loan_entity.manager;

import java.io.Serializable;
import java.util.Date;

/**
 * 个人信息表 
 * @author xuepengfei
 *2016年9月28日上午11:47:02
 */
public class PrivateOld implements Serializable {
    private Integer id;
    private Integer per_id;
    private String qq_num;
    private String email;
    private String usuallyaddress;
    private String education;
    private String marry;
    private String getchild;
    private String profession;
    private String monthlypay;
    private String business;
    private String busi_province;
    private String busi_city;
    private String busi_address;
    private String busi_phone;
    private String relatives;
    private String relatives_name;
    private String rela_phone;
    private String society;
    private String soci_phone;
    private String society_name;
    private Date updateDate;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getPer_id() {
        return per_id;
    }
    public void setPer_id(Integer per_id) {
        this.per_id = per_id;
    }
    public String getQq_num() {
        return qq_num;
    }
    public void setQq_num(String qq_num) {
        this.qq_num = qq_num;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsuallyaddress() {
        return usuallyaddress;
    }
    public void setUsuallyaddress(String usuallyaddress) {
        this.usuallyaddress = usuallyaddress;
    }
    public String getEducation() {
        return education;
    }
    public void setEducation(String education) {
        this.education = education;
    }
    public String getMarry() {
        return marry;
    }
    public void setMarry(String marry) {
        this.marry = marry;
    }
    public String getGetchild() {
        return getchild;
    }
    public void setGetchild(String getchild) {
        this.getchild = getchild;
    }
    public String getProfession() {
        return profession;
    }
    public void setProfession(String profession) {
        this.profession = profession;
    }
    public String getMonthlypay() {
        return monthlypay;
    }
    public void setMonthlypay(String monthlypay) {
        this.monthlypay = monthlypay;
    }
    public String getBusiness() {
        return business;
    }
    public void setBusiness(String business) {
        this.business = business;
    }
    public String getBusi_province() {
        return busi_province;
    }
    public void setBusi_province(String busi_province) {
        this.busi_province = busi_province;
    }
    public String getBusi_city() {
        return busi_city;
    }
    public void setBusi_city(String busi_city) {
        this.busi_city = busi_city;
    }
    public String getBusi_address() {
        return busi_address;
    }
    public void setBusi_address(String busi_address) {
        this.busi_address = busi_address;
    }
    public String getBusi_phone() {
        return busi_phone;
    }
    public void setBusi_phone(String busi_phone) {
        this.busi_phone = busi_phone;
    }
    public String getRelatives() {
        return relatives;
    }
    public void setRelatives(String relatives) {
        this.relatives = relatives;
    }
    public String getRelatives_name() {
        return relatives_name;
    }
    public void setRelatives_name(String relatives_name) {
        this.relatives_name = relatives_name;
    }
    public String getRela_phone() {
        return rela_phone;
    }
    public void setRela_phone(String rela_phone) {
        this.rela_phone = rela_phone;
    }
    public String getSociety() {
        return society;
    }
    public void setSociety(String society) {
        this.society = society;
    }
    public String getSoci_phone() {
        return soci_phone;
    }
    public void setSoci_phone(String soci_phone) {
        this.soci_phone = soci_phone;
    }
    public String getSociety_name() {
        return society_name;
    }
    public void setSociety_name(String society_name) {
        this.society_name = society_name;
    }
    public Date getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    

}
