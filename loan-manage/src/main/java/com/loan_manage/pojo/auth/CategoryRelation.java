package com.loan_manage.pojo.auth;

import java.util.Arrays;

/**
 * 描述组织管理权限信息
 *
 * @author chenchao
 *         Created by chenchao on 2017/10/19.
 */
public class CategoryRelation {
    private String category;
    private String[] privileges;

    public CategoryRelation(Category category, String... privileges) {
        this.category = category.getType();
        this.privileges = privileges;
    }

    public CategoryRelation(Category category, Category... privileges) {
        this.category = category.getType();
        this.privileges = new String[privileges.length];
        for (int i = 0; i < privileges.length; i++) {
            this.privileges[i] = privileges[i].getType();
        }
    }

    public String getCategory() {
        return category;
    }

    public String[] getPrivileges() {
        return privileges;
    }
}
