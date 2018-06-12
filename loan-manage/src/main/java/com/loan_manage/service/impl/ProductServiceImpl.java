package com.loan_manage.service.impl;

import java.util.List;

import com.loan_entity.manager.ProductMVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loan_entity.app.Product;
import com.loan_manage.mapper.ProductMapper;
import com.loan_manage.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Override
    public List<Product> selectAllProduct() {
        return productMapper.selectAll();
    }
	@Override
	public List<Product> getProductAndTeam() {
		return productMapper.getProductAndTeam();
	}

    @Override
    public List<ProductMVo> selectProductInfoToRedis() {
        return productMapper.selectProductInfo();
    }
}
