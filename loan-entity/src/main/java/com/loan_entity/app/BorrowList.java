package com.loan_entity.app;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="ym_borrow_list")
@Getter @Setter
@ToString
public class BorrowList implements Serializable {
	
	private static final long serialVersionUID = 2510888469306589931L;
    @Id
	private Integer id;

    private Integer perId;

    private Integer prodId;

    private String borrType;

    private Date askborrDate;

    private String borrNum;

    private Date makeborrDate;

    private Date payDate;

    private Date planrepayDate;

    private Date actRepayDate;

    private String borrStatus;

    private String planRepay;

    private String actRepayAmount;

    private Integer ispay;

    private Integer termId;

    private String borrAmount;

    private String perCouponId;


    private Date updateDate;

    private Integer updateUser;

    private Date creationDate;

    private Integer creationUser;

    private String sync;

    @Transient
    private String termValue;
    @Transient
    private String maximumAmount;
    @Transient
    private String borrStatusName;
    @Transient
    private String repayDate;

    private Integer version;

    private Integer baikeluStatus;
}