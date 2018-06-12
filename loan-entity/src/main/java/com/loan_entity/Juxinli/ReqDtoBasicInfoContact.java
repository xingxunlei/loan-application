package com.loan_entity.Juxinli;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by xuepengfei on 2017/10/19.
 */
@Data
public class ReqDtoBasicInfoContact implements Serializable {
    /**联系人电话**/
    private String contact_tel;
    /**联系人姓名**/
    private String contact_name;
    /**联系人类型（"0":配偶，"1":父母，"2":兄弟姐妹,"3":子女,"4":同事,"5": 同学,"6": 朋友）字符串类型非int**/
    private String contact_type;
}
