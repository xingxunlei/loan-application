package com.loan_entity.contract;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by wanzezhong on 2017/11/28.
 */
@Getter
@Setter
@Entity
@Table(name = "ym_contract")
public class Contract implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer borrId;

    private String  borrNum;

    private Integer status;

    private String contractUrl;

    private String resultJson;

    private Date createDate;

    private Date updateDate;

    private String productId;
}
