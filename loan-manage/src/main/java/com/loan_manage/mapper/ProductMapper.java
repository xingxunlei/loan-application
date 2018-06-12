package com.loan_manage.mapper;

import java.util.List;

import com.loan_entity.manager.ProductMVo;
import tk.mybatis.mapper.common.Mapper;

import com.loan_entity.app.Product;

public interface ProductMapper extends Mapper<Product> {
	
	List<Product> getProductAndTeam();

	/**
	 * 查询产品信息-redis用
	 * @return
	 */
	List<ProductMVo> selectProductInfo();
}
