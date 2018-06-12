package com.loan_server.app_mapper;

import java.util.List;

import com.loan_entity.app.PerCoupon;
import com.loan_entity.app.PerCoupon2;

public interface PerCouponMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PerCoupon record);

    int insertSelective(PerCoupon record);

    PerCoupon selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PerCoupon record);

    int updateByPrimaryKey(PerCoupon record);
    
    //根据per_id，产品id,使用状态查询优惠券
    List<PerCoupon> selectByPerProductStatus(Integer per_id,Integer product_id,String status);
    //查询未使用的优惠券
    List<PerCoupon2> getMyCoupon(String userId,int start,int pageSize);

    // 查询失效的优惠券
    List<PerCoupon2> getMyCoupon2(String userId,int start,int pageSize);

    // 根据id查询coupon2
    PerCoupon2 getCoupon2ById(String id);

    //根据per_id，产品id,使用状态查询优惠券
    List<PerCoupon> selectByPerIdStatus(String per_id,String status);
}