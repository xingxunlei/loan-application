package com.loan_manage.mapper;

import com.loan_entity.manager.Coupon;
import com.loan_entity.manager_vo.CouponVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CouponMapper extends Mapper<Coupon> {

    List<CouponVo> getAllCouponList();
    
    Coupon selectByName(String couponName);
}