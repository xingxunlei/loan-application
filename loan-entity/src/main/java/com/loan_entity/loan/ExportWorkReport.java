package com.loan_entity.loan;

import com.loan_entity.enums.DclTypeEnum;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

/**
 * Created by wanzezhong on 2017/7/17.
 */
@ExcelTarget("exportWorkReport")
public class ExportWorkReport {

    @Excel(name="催收人员   ",width=40,orderNum="3")
    private String bedueName ;
    @Excel(name="总催收数量   ",width=40,orderNum="3")
    private String sumDone;
    @Excel(name="未完成数量",width=40,orderNum="3")
    private String unDone ;
    @Excel(name="已完成数量",width=40,orderNum="3")
    private String done ;
    @Excel(name="催收员类型",width=40,orderNum="3")
    private String dclTypeStr;

    private Integer dclType;
    private Integer levelType;
    private String levelTypeStr;


    public String getBedueName() {
        return bedueName;
    }

    public void setBedueName(String bedueName) {
        this.bedueName = bedueName;
    }

    public String getSumDone() {
        return sumDone;
    }

    public void setSumDone(String sumDone) {
        this.sumDone = sumDone;
    }

    public String getUnDone() {
        return unDone;
    }

    public void setUnDone(String unDone) {
        this.unDone = unDone;
    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }

    public Integer getDclType() {
        return dclType;
    }

    public void setDclType(Integer dclType) {
        this.dclType = dclType;
        dclTypeStr = DclTypeEnum.getDescByCode(dclType+"");
    }

    public String getDclTypeStr() {
        return dclTypeStr;
    }

    public void setDclTypeStr(String dclTypeStr) {
        this.dclTypeStr = dclTypeStr;
    }

    public Integer getLevelType() {
        return levelType;
    }

    public void setLevelType(Integer levelType) {
        this.levelType = levelType;
        this.levelTypeStr = DclTypeEnum.getDescByCode(levelType+"");
    }

    public String getLevelTypeStr() {
        return levelTypeStr;
    }

    public void setLevelTypeStr(String levelTypeStr) {
        this.levelTypeStr = levelTypeStr;
    }
}
