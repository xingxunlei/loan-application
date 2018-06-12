package com.jhh.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.jhh.model.BaiKeLuReport;

public interface BaiKeLuReportMapper {
	
	List<BaiKeLuReport> getShenHe(@Param("limit") int limit,@Param("startdate") String startdate);
	
	List<BaiKeLuReport> getTiShi(@Param("limit") int limit);

}
