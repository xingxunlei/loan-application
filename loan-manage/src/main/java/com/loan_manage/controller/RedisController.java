package com.loan_manage.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.loan_entity.app.Product;
import com.loan_manage.service.ProductService;
import com.loan_manage.service.RedisService;
import com.loan_manage.utils.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/redis")
public class RedisController {
    @Autowired
    private ProductService productService;
    @Autowired
    private RedisService redisService;
    @RequestMapping("/redisSetProduct") @ResponseBody
    public String redisSetProduct(){
        List<Product> products = productService.selectAllProduct();
        JSONArray array = new JSONArray();
        for(Product product : products){
            JSONObject object = new JSONObject();
            object.put("id",product.getId());
            object.put("name",product.getProductName());
            array.add(object);
        }
        JedisUtil.set("product",array.toJSONString());
        return "success";
    }

    @RequestMapping("/redisGetProduct") @ResponseBody
    public JSONObject redisGetProduct(@RequestParam("productId") int productId){
        return redisService.selectProductFromRedis(productId);
    }
}
