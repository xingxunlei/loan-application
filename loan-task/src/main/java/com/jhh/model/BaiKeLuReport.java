package com.jhh.model;

import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhh.util.DateUtil;

import lombok.Data;

@Data
public class BaiKeLuReport {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final int All = 0;
	
	public static final int SHEN_HE = 1;
	
	public static final int TI_SHI = 2;

	private Date day;

	private int type;
	
	private int shenHe;
	
	private int tiShi;
	
	private int count;
	
	public String getDayString() {
		return DateUtil.getDateString(day);
	}
	
	public void join(BaiKeLuReport o) {
		if(!this.getDayString().equals(o.getDayString())) {
			logger.warn(this.getDayString() + " : " + o.getDayString());
			throw new IllegalArgumentException("只能join同一天的数据");
		}
		if(this.type == All) {
			throw new IllegalArgumentException("All的数据不需要join");
		}
		if(this.type == o.getType()) {
			throw new IllegalArgumentException("只能join不同类型的数据");
		}
		switch(o.getType()) {
		case SHEN_HE: this.shenHe = o.getShenHe();break;
		case TI_SHI: this.tiShi = o.getTiShi(); break;
		case All: throw new IllegalArgumentException("不能join类型为All的数据");
		default: break;
		}
		this.type = All;
	}
	
	public void setType(int type) {
		this.type = type;
		switch(this.type) {
		case SHEN_HE: this.shenHe = count;break;
		case TI_SHI: this.tiShi = count; break;
		default: break;
		}
	}
	
}
