package com.loan_manage.entity.arbitration;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by wanzezhong on 2018/3/27.
 */

@Getter
@Setter
public class ClaimItemsExtData implements Serializable {

    /*****欠款本金*****/
    public String principle;
    /*****核算截止之日*****/
    public String accountingDate;
    /*****截至仲裁天数*****/
    public String accountingDays;
    /*****请求逾期利息总额*****/
    public String liquidatedDamages1;
    /*****请求违约金总额*****/
    public String liquidatedDamages2;
}
