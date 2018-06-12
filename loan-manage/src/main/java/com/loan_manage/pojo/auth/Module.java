package com.loan_manage.pojo.auth;

import java.util.List;

/**
 * Created by chenchao on 2017/10/13.
 */
public interface Module {
    public String getId();

    public String getDesc();

    public String getIndex();

    public String getType();

    public List<Role> getRoles();
}
