package com.loan_entity.manager;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
/**公司合同*/
@Entity @Table(name = "ym_zloan_company_borrow") @Getter @Setter
public class LoanCompanyBorrow {
    /**
     * ID
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 合同ID
     */
    private Integer borrId;
    /**
     * 公司ID
     */
    private Integer companyId;
    /**
     * 创建人
     */
    private String createUser;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 更新人
     */
    private String updateUser;
    /**
     * 更新时间
     */
    private Date updateDate;
}
