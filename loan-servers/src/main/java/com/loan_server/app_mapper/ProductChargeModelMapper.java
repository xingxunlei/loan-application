package com.loan_server.app_mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loan_entity.app.ProductChargeModel;

public interface ProductChargeModelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductChargeModel record);

    int insertSelective(ProductChargeModel record);

    ProductChargeModel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductChargeModel record);

    int updateByPrimaryKey(ProductChargeModel record);
       
    //根据产品ID查询所有收费表  
    List<ProductChargeModel> selectByProductId(@Param("product_id")Integer product_id);
}