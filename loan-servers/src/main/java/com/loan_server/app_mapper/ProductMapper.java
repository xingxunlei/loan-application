package com.loan_server.app_mapper;

import java.util.List;

import com.loan_entity.app.Product;
import com.loan_entity.app_vo.ProductApp;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);
    
    List<Product> getAllProduct();
    
    List<ProductApp> getAllProductAvailable();

}