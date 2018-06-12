package com.loan_manage.pojo.auth;

/**
 * Created by chenchao on 2017/10/13.
 */
public enum Category {
    POST_LOAN("1", "贷后部"),
    OUTSOURCE("2", "外包催收"),
    RISK_MANAGEMENT("3", "风控部"),
    OPERATION("4", "运营部"),
    CUSTOM_SERVICE("5", "客服部"),
    DEVELOPER("6", "研发部"),
    SYSTEM_ADMIN("10", "系统管理");

    private Category(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private String desc;
    private String type;


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
