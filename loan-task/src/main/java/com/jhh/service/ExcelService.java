package com.jhh.service;

import org.apache.poi.ss.usermodel.Workbook;

public interface ExcelService {
	
	Workbook sqlToExcel(String sql);

}
