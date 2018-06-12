package com.loan_manage.pojo.auth;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.EnumSerializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by chenchao on 2017/10/13.
 */
public class RoleGenerator {

    public static void main(String[] args) {
        new RoleGenerator();
    }

    public RoleGenerator() {
        Category[] categorys = Category.values();
        JSONArray array = new JSONArray();
        for (int i = 0; i < categorys.length; i++) {
            Category category = categorys[i];
            JSONObject object = new JSONObject();
            object.put("desc", category.getDesc());
            object.put("type", category.getType());
            JSONArray roles = new JSONArray();
            Arrays.stream(Role.values()).filter(t -> t.getType().equals(category.getType())).forEach(t -> {
                JSONObject role = new JSONObject();
                role.put("desc", t.getDesc());
                role.put("level", t.getLevel());
                roles.add(role);
            });
            object.put("roles", roles);
            array.add(i, object);
        }
        System.out.println(array.toJSONString());
    }
}
