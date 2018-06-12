package com.loan_server.loan_service.coupon.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.loan_api.app.UserService;
import com.loan_entity.app.PerCoupon;
import com.loan_entity.manager.Coupon;
import com.loan_entity.utils.BorrPerInfo;
import com.loan_server.app_mapper.PerCouponMapper;
import com.loan_server.loan_service.coupon.CouponService;
import com.loan_server.manager_mapper.CouponMapper;
import com.loan_utils.util.Assertion;

@Service("couponService")
public class CouponServiceImpl implements CouponService {
	private static final Logger logger = LoggerFactory
			.getLogger(CouponServiceImpl.class);

	@Autowired
	private CouponMapper couponMapper;

	@Autowired
	private PerCouponMapper perCouponMapper;

	@Autowired
	private UserService userService;

	public void grantCoupon(String couponName, int userId, BorrPerInfo bpi) {
		logger.info("grantCoupon:start");
		Assertion.notEmpty(couponName, "优惠券名称不能为空");
		Assertion.isPositive(userId, "赠送用户不能为空");
		Assertion.notNull(bpi, "合同和人的信息不能为空");
		Coupon cou = this.couponMapper.selectByName(couponName);
		
		logger.info("grantCoupon coupon:" + JSON.toJSONString(cou).toString());
		if (cou != null) {
			if ("1".equals(cou.getStatus())) {
				PerCoupon pc = new PerCoupon();
				pc.setPerId(Integer.valueOf(userId));
				pc.setCouponId(cou.getId());
				pc.setCouponName(cou.getCouponName());
				pc.setProductId(cou.getProductId());
				pc.setStartDate(new Date());

				Date date = pc.getStartDate();
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				calendar.add(5, cou.getDuation().intValue());
				date = calendar.getTime();
				pc.setEndDate(date);

				pc.setAmount(cou.getAmount().toString());
				pc.setStatus("1");

				pc.setCreationUser(Integer.valueOf(userId));
				pc.setCreationDate(new Date());
				pc.setUpdateUser(Integer.valueOf(userId));
				pc.setUpdateDate(new Date());
				this.perCouponMapper.insertSelective(pc);

				logger.info("优惠券发放完毕，发送站内信开始--");
				String result = this.userService.setMessage(
						String.valueOf(userId), "8",
						bpi.getName() + "," + pc.getCouponName());
				JSONObject obje = JSONObject.fromObject(result);
				if ("200".equals(obje.get("code")))
					logger.info("发放优惠券，消息发送成功！");
				else
					logger.info(obje.get("info").toString());
			}
		}
	}
}