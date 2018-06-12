package com.loan_entity.manager_vo;

import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * Create by Jxl on 2017/9/15
 */
public class PhoneBookVo implements Serializable {

    @Excel(name="电话号码",width = 14.0)
    private String phone;
    @Excel(name="姓名")
    private String name;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
