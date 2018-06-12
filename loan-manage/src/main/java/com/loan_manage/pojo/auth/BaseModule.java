package com.loan_manage.pojo.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchao on 2017/10/13.
 */
public abstract class BaseModule implements Module {
    protected String id;
    protected String desc;
    protected String index;
    protected String type;
    protected List<Role> roles = new ArrayList<Role>();

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        if (roles != null) {
            this.roles = roles;
        }
    }

    public void addRole(Role role) {
        if (roles != null && !roles.contains(role)) {
            roles.add(role);
        }
    }

    public boolean removeRole(Role role) {
        if (roles != null && roles.contains(role)) {
            return roles.remove(role);
        }
        return false;
    }
}
