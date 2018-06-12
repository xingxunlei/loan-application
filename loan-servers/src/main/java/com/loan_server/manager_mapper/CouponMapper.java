package com.loan_server.manager_mapper;

import java.util.List;

import com.loan_entity.manager.Coupon;
import com.loan_entity.manager_vo.CouponVo;
import com.loan_entity.manager_vo.QuestionVo;

public interface CouponMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Coupon record);

    int insertSelective(Coupon record);

    Coupon selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Coupon record);

    int updateByPrimaryKey(Coupon record);
    List<CouponVo> getAllCouponList();
    
    Coupon selectByName(String couponName);
}