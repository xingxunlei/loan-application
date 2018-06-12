package com.loan_manage.pojo.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchao on 2017/10/13.
 */
public class Page {
    private String id;
    private String desc;
    private String pageUrl;
    private List<Role> roles = new ArrayList<Role>();
    private List<Module> modules = new ArrayList<Module>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

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

    public void setModules(List<Module> modules) {
        if (modules != null) {
            this.modules = modules;
        }
    }

    public void addModule(Module module) {
        if (modules != null && !modules.contains(module)) {
            modules.add(module);
        }
    }

    public List<Module> getModules() {
        return modules;
    }
}
