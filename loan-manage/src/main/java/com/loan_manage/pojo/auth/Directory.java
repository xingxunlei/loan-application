package com.loan_manage.pojo.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchao on 2017/10/13.
 */
public class Directory {
    private String id;
    private String desc;
    private List<Role> roles = new ArrayList<Role>();
    private List<Directory> subDirs = new ArrayList<Directory>();
    private List<Page> pages = new ArrayList<Page>();


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

    public List<Directory> getSubDirs() {
        return subDirs;
    }

    public void setSubDirs(List<Directory> subDirs) {
        if (subDirs != null) {
            this.subDirs = subDirs;
        }
    }

    public void addSubDir(Directory subDir) {
        if (subDir != null && !subDirs.contains(subDir)) {
            subDirs.add(subDir);
        }
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        if (pages != null) {
            this.pages = pages;
        }
    }

    public void addPage(Page page) {
        if (page != null && !pages.contains(page)) {
            pages.add(page);
        }
    }
}
