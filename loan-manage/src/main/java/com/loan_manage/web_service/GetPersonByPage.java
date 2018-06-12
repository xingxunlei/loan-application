
package com.loan_manage.web_service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getPersonByPage", propOrder = {
    "pageindex",
    "pagesize"
})
public class GetPersonByPage {

    protected Integer pageindex;
    protected Integer pagesize;

    public Integer getPageindex() {
        return pageindex;
    }

    public void setPageindex(Integer value) {
        this.pageindex = value;
    }

    public Integer getPagesize() {
        return pagesize;
    }

    public void setPagesize(Integer value) {
        this.pagesize = value;
    }

}
