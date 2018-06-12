package com.loan_manage.entity.arbitration;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wanzezhong on 2018/3/27.
 */
@Getter @Setter
public class TemplateJson implements Serializable{
    String prdsNum;
    Claim claim;
    List<Evidences> evidences;
}
