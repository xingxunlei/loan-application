package com.jhh.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jhh.service.ExcelService;

@Component
public class ExcelServiceImpl implements ExcelService{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Override
	public Workbook sqlToExcel(String sql) {
		if(logger.isDebugEnabled()) {
			logger.debug(sql);
		}
		Connection conn = sqlSessionFactory.openSession().getConnection();
		Statement stat = null;
		ResultSet rs = null;
		try {
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
			Workbook wb = new SXSSFWorkbook();
			Sheet sheet = wb.createSheet();
			ResultSetMetaData rsmd = rs.getMetaData();
			int rowNo = 0;
			Row row = sheet.createRow(rowNo);
			for(int i = 0;i < rsmd.getColumnCount();i ++) {
				row.createCell(i).setCellValue(rsmd.getColumnLabel(i+1));
			}
			rs.beforeFirst();
			while(rs.next()) {
				rowNo++;
				row = sheet.createRow(rowNo);
				for(int i = 0;i < rsmd.getColumnCount();i ++) {
					row.createCell(i).setCellValue(rs.getString(i+1));
				}
			}
			return wb;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			if(conn != null)try {conn.close();} catch (SQLException e) {logger.error("", e);}
			if(stat != null)try {stat.close();} catch (SQLException e) {logger.error("", e);}
			if(rs != null)try {rs.close();} catch (SQLException e) {logger.error("", e);}
		}
	}

}
