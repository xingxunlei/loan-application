package com.loan_entity.Juxinli;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

    /**
     *聚信立请求实体
     */
    @Data
    public class ReqDtoBasicInfo implements Serializable{

        /**per_id**/
        private String per_id;
        /**名称**/
        private String name;
        /**身份证号码**/
        private String id_card_num;
        /**手机号码**/
        private String cell_phone_num;
        /**家庭地址**/
        private String home_addr;
        /**工作电话**/
        private String work_tel;
        /**工作地址**/
        private String work_addr;
        /**家庭电话**/
        private String home_tel;
        /**备用电话**/
        private String cell_phone_num2;
        /**联系人信息**/
        private List<ReqDtoBasicInfoContact> baseInfoContacts;

        /**服务密码**/
        private String password;
        /**验证码**/
        private String captcha;
        /**查询密码**/
        private String queryPwd;
        /**code编码**/
        private String code;
        /**获取通讯录链接url**/
        private String relation_contacts_url;
        /**是否白名单**/
        private Integer whiteTag;
        /**请求Id  youmi/jinhudai**/
        private String requestId;
    }

