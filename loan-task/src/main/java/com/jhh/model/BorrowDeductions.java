package com.jhh.model;

import com.jhh.util.Detect;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "ym_borrow_deductions")
@Getter @Setter
public class BorrowDeductions implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer borrId;//5101177
    private Integer perId;
    private Integer status;
    private String reason;
    private Date createDate;
    private Date updateDate;
    @Transient
    private String state; //冗余判断

    public void setReason(String reason) {
        this.reason = reason;
        if(!Detect.isPositive(status)){
            if(state != null && "s".equals(state)){
                this.status = 1;
            }else {
                if(matchString(reason)){
                    this.status = 2;
                }else {
                    this.status = 3;
                }
            }
        }
    }


    private boolean matchString( String str ) {
        Pattern datePattern = Pattern.compile("查|权|交易失败|额|密|道|订");
        Matcher dateMatcher = datePattern.matcher(str);
        return dateMatcher.find();
    }

}
