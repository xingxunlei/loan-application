package com.loan_manage.mapper;

import java.util.List;
import java.util.Map;

import tk.mybatis.mapper.common.Mapper;

import com.loan_entity.app.PerCoupon;

public interface PerCouponMapper extends Mapper<PerCoupon> {
	
	List<Map<String, Object>> getCoupon(String couponId);
}
