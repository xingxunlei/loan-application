package com.loan_manage.pojo.auth;

/**
 * Created by chenchao on 2017/10/13.
 */
public enum Role {
    POST_LOAN_MANAGER(Category.POST_LOAN, "6", "贷后管理"),
    POST_LOAN_NORMAL(Category.POST_LOAN, "0", "贷后员工"),
    POST_LOAN_FINANCE(Category.POST_LOAN, "1", "贷后财务专用"),
    POST_LOAN_SENIOR(Category.POST_LOAN, "3", "贷后高级催收"),
    OUTSOURCE_MANAGER(Category.OUTSOURCE, "6", "外包管理"),
    OUTSOURCE_NORMAL(Category.OUTSOURCE, "0", "外包员工"),
    RISK_MANAGEMENT_DIRECTOR(Category.RISK_MANAGEMENT, "9", "风控总监"),
    RISK_MANAGEMENT_MANAGER(Category.RISK_MANAGEMENT, "6", "风控管理"),
    RISK_MANAGEMENT_DATA(Category.RISK_MANAGEMENT, "1", "风控数据专员"),
    RISK_MANAGEMENT_NORMAL(Category.RISK_MANAGEMENT, "0", "风控员工"),
    OPERATION_MANAGER(Category.OPERATION, "6", "运营管理"),
    OPERATION_NORMAL(Category.OPERATION, "0", "运营员工"),
    CUSTOM_SERVICE_MANAGER(Category.CUSTOM_SERVICE, "6", "客服管理"),
    CUSTOM_SERVICE_NORMAL(Category.CUSTOM_SERVICE, "0", "客服员工"),
    DEVELOPER_MANAGER(Category.DEVELOPER, "6", "研发管理"),
    DEVELOPER_NORMAL(Category.DEVELOPER, "0", "研发员工"),
    SYSTEM_ADMIN(Category.SYSTEM_ADMIN, "6", "系统管理员");

    private Role(Category category, String level, String desc) {
        this.type = category.getType();
        this.level = level;
        this.desc = desc;
    }

    private String desc;
    private String type;
    private String level;


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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return type + "." + level;
    }
}
