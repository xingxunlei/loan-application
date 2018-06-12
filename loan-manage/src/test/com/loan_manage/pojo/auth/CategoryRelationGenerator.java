package com.loan_manage.pojo.auth;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchao on 2017/10/19.
 */
public class CategoryRelationGenerator {
    public static void main(String[] args) {
        new CategoryRelationGenerator();
    }

    public CategoryRelationGenerator() {
        JSONArray array = new JSONArray();
        array.add(new CategoryRelation(Category.POST_LOAN, Category.POST_LOAN, Category.OUTSOURCE));
        array.add(new CategoryRelation(Category.OUTSOURCE, Category.OUTSOURCE));
        array.add(new CategoryRelation(Category.RISK_MANAGEMENT, Category.RISK_MANAGEMENT));
        array.add(new CategoryRelation(Category.OPERATION, Category.OPERATION));
        array.add(new CategoryRelation(Category.DEVELOPER, Category.values()));
        array.add(new CategoryRelation(Category.SYSTEM_ADMIN, Category.values()));

        System.out.println(array.toJSONString());
    }
}
