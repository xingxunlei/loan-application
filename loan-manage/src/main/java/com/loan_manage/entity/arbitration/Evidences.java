package com.loan_manage.entity.arbitration;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wanzezhong on 2018/3/27.
 */
@Getter @Setter
public class Evidences implements Serializable {
    /*****申请方******/
    public String providerType;
    /*****方伟******/
    public String provider;
    public List<EvidenceItems> evidenceItems;
}
