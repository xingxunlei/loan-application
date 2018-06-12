package com.loan_manage.service;

import java.util.List;

import com.loan_entity.app.Product;
import com.loan_entity.manager.ProductMVo;

public interface ProductService {
    List<Product> selectAllProduct();

    List<Product> getProductAndTeam();

    List<ProductMVo> selectProductInfoToRedis();
}
