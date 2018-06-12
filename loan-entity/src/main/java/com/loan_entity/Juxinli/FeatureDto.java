package com.loan_entity.Juxinli;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by xuepengfei on 2017/10/19.
 */
@Data
public class FeatureDto implements Serializable{

    /**标记为1 则表示该信息为白名单用户 ，将不会受风控规则影响，走完流程**/
    private int whiteTag;
    /**回调地址**/
    private String callbackURI;
}
