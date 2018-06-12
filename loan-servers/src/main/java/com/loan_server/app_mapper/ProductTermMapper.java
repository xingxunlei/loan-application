package com.loan_server.app_mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.loan_entity.app.ProductTerm;

public interface ProductTermMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductTerm record);

    int insertSelective(ProductTerm record);

    ProductTerm selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductTerm record);

    int updateByPrimaryKey(ProductTerm record);
    
    /**
     * 根据产品ID查询期数表
     * @param productId
     * @return
     */
    ProductTerm selectByProductId(@Param("product_id")Integer product_id);
    
    /**
     * 查询所有产品金额
     * @return
     */
    List<Long> findProductMoney();
    
    /**
     * 根据金额查询天数
     * @return
     */
    List<Integer> findProductDay(@Param("money")Long money);
    
    /**
     * 根据金额和天数查询产品ID
     * @return
     */
    Integer findProductId(String money,String day);
    
    
}