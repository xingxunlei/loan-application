package com.loan_manage.entity;

import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * Created by wanzezhong on 2017/7/28.
 */
public class PolyXinLi implements Comparable<PolyXinLi>{
    @Excel(name="手机号码",width = 14.0)
    private String phone_num;
    @Excel(name="互联网标识",width = 14.0)
    private String contact_name;
    @Excel(name="归属地",width = 14.0)
    private String phone_num_loc;
    @Excel(name="联系次数",width = 14.0)
    private Integer call_cnt;
    @Excel(name="联系时间",width = 14.0)
    private String call_len;
    @Excel(name="主叫次数",width = 14.0)
    private Integer call_out_cnt;
    @Excel(name="被叫次数",width = 14.0)
    private Integer call_in_cnt;
    @Excel(name="通讯录姓名",width = 14.0)
    private String YMName;

    @Override
    public int compareTo(PolyXinLi o) {
        if (call_cnt > o.getCall_cnt()) //这里比较的是什么 sort方法实现的就是按照此比较的东西从小到大排列
            return - 1 ;
        if (call_cnt < o.getCall_cnt())
            return 1 ;
        return 0;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getPhone_num_loc() {
        return phone_num_loc;
    }

    public void setPhone_num_loc(String phone_num_loc) {
        this.phone_num_loc = phone_num_loc;
    }

    public Integer getCall_cnt() {
        return call_cnt;
    }

    public void setCall_cnt(Integer call_cnt) {
        this.call_cnt = call_cnt;
    }

    public String getCall_len() {
        return call_len;
    }

    public void setCall_len(String call_len) {
        this.call_len = call_len;
    }

    public Integer getCall_out_cnt() {
        return call_out_cnt;
    }

    public void setCall_out_cnt(Integer call_out_cnt) {
        this.call_out_cnt = call_out_cnt;
    }

    public Integer getCall_in_cnt() {
        return call_in_cnt;
    }

    public void setCall_in_cnt(Integer call_in_cnt) {
        this.call_in_cnt = call_in_cnt;
    }

    public String getYMName() {
        return YMName;
    }

    public void setYMName(String YMName) {
        this.YMName = YMName;
    }
}
