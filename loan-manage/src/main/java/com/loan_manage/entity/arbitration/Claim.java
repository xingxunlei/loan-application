package com.loan_manage.entity.arbitration;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wanzezhong on 2018/3/27.
 */

@Getter
@Setter
public class Claim implements Serializable {
    /*****陈述理由*****/
    public String reason;
    /*****文件*****/
    public List<Files> files;
    /*****扩展信息*****/
    public ExtData extData;
    /*****仲裁请求*****/
    public List<ClaimItems> claimItems;

}
