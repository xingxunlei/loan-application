package com.loan_entity.drainage;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zhangqi on 17-11-20.
 */
@Setter
@Getter
public class Drainage implements Serializable{


    private int id;
    /**
     * 引流产品的logo 展示图
     */
    private String drainage_img;
    /**
     * 引流产品的名字
     */
    private String drainage_name;
    /**
     * 引流产品的标题
     */
    private String drainage_title;
    /**
     * 引流产品的说明
     */
    private String drainage_des;
    /**
     * 引流产品的类型
     */
    private String drainage_type;
    /**
     * 引流产品的 跳转地址
     */
    private String drainage_url;
    /**
     * 引流产品的成功回调地址
     */
    private String drainage_backurl;
    /**
     * 引流产品的状态
     */
    private int drainage_status;
    /**
     * 创建日期
     */
    private Date creation_date;
    /**
     * 更新日期
     */
    private Date update_date;
}
