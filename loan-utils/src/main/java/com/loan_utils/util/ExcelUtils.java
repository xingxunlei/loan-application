package com.loan_utils.util;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.jeecgframework.poi.excel.entity.params.ExcelExportEntity;
import org.jeecgframework.poi.excel.entity.vo.MapExcelConstants;
import org.jeecgframework.poi.excel.entity.vo.NormalExcelConstants;
import org.jeecgframework.poi.excel.export.ExcelExportServer;

public class ExcelUtils {

	protected static final String HSSF  = ".xls";
	protected static final String ISO8859_1="ISO8859-1";
    protected static final String UTF8 = "UTF-8";
    protected static final String XSSF  = ".xlsx";
	public static String SPLIT_STR="@!@";
	
	public static void excelWriteDownloadFile(Workbook workbook, String fileName, HttpServletRequest request, HttpServletResponse response) {
		// 设置输出的格式
		ServletOutputStream sos = null;
		ByteArrayOutputStream buffer = null ;
		try {
			sos = response.getOutputStream();
			response.getOutputStream();
			buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/octet-stream;charset=GBK");
			response.setContentLength(buffer.size());
			//火狐
	        if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
	        	fileName = new String(fileName.getBytes(UTF8), ISO8859_1);
	        }else{
	        	fileName = URLEncoder.encode(fileName, UTF8);
	        }
            response.setHeader("Content-Disposition", "attachment;filename=\""+fileName+"\"");
			sos.write(buffer.toByteArray());
			buffer.flush();
		} catch (IOException e) {
			e.printStackTrace();
//			closeStream(sos, buffer);
		} finally {
			closeStream(sos, buffer);
		}
	}

	private static void closeStream(ServletOutputStream sos, ByteArrayOutputStream buffer) {
		try {
            if (buffer != null) {
                buffer.flush();
                buffer.close();
            }
            if (sos != null) {
                sos.flush();
                sos.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
	}

	/**
	 * 将对象属性值转换成Excel文件输出
	 * @param model
	 * @param request
	 * @param response
	 */
	public static void jeecgSingleExcel(Map<String, Object> model,HttpServletRequest request,HttpServletResponse response){
		 String codedFileName = "";
	        Workbook workbook = getWorkbook(model);
	        
	        if (model.containsKey(NormalExcelConstants.FILE_NAME)) {
	            codedFileName = (String) model.get(NormalExcelConstants.FILE_NAME);
	        }
	        if (workbook instanceof HSSFWorkbook) {
	            codedFileName += HSSF;
	        } else {
	            codedFileName += XSSF;
	        }
	        excelWriteDownloadFile(workbook, codedFileName, request, response);
	}

	
	public void jeecgMapExcel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String codedFileName = "";
		Workbook workbook = ExcelExportUtil.exportExcel(
				(ExportParams) model.get(MapExcelConstants.PARAMS),
				(List<ExcelExportEntity>) model.get(MapExcelConstants.ENTITY_LIST),
				(Collection<? extends Map<?, ?>>) model.get(MapExcelConstants.MAP_LIST));
		if (model.containsKey(MapExcelConstants.FILE_NAME)) {
			codedFileName = (String) model.get(MapExcelConstants.FILE_NAME);
		}
		if (workbook instanceof HSSFWorkbook) {
			codedFileName += HSSF;
		} else {
			codedFileName += XSSF;
		}
		excelWriteDownloadFile(workbook, codedFileName, request, response);
	}

	@SuppressWarnings("unchecked")
	public static Workbook getWorkbook(Map<String, Object> model){
		Workbook workbook = null;
		
		if (model.containsKey(NormalExcelConstants.MAP_LIST)) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) model.get(NormalExcelConstants.MAP_LIST);
            if (list.size() == 0) {
                throw new RuntimeException("MAP_LIST IS NULL");
            }
            workbook = ExcelExportUtil.exportExcel(
                (ExportParams) list.get(0).get(NormalExcelConstants.PARAMS), (Class<?>) list.get(0)
                    .get(NormalExcelConstants.CLASS),
                (Collection<?>) list.get(0).get(NormalExcelConstants.DATA_LIST));
            for (int i = 1; i < list.size(); i++) {
                new ExcelExportServer().createSheet(workbook,
                    (ExportParams) list.get(i).get(NormalExcelConstants.PARAMS), (Class<?>) list
                        .get(i).get(NormalExcelConstants.CLASS),
                    (Collection<?>) list.get(i).get(NormalExcelConstants.DATA_LIST));
            }
        } else {
            workbook = ExcelExportUtil.exportExcel(
                (ExportParams) model.get(NormalExcelConstants.PARAMS),
                (Class<?>) model.get(NormalExcelConstants.CLASS),
                (Collection<?>) model.get(NormalExcelConstants.DATA_LIST));
        }
		
		return workbook;
	}
	
	public static void saveFile(Workbook workbook, String path, String fileName) throws FileNotFoundException {
		Assertion.notEmpty(path, "保存路径不能为空");
		Assertion.notEmpty(fileName, "保存文件名不能为空");
		
		//判断文件夹
		File fileDir = new File(path);
		//判断文件
		File file = new File(path + fileName);
		
		judeDirExists(fileDir);
		judeFileExists(file);
		
		// 保存Excel文件
		FileOutputStream fileOut = null ;
		try {
			fileOut = new FileOutputStream(file);
			workbook.write(fileOut);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	// 判断文件是否存在
	public static void judeFileExists(File file) {
		if (file.exists()) {
			System.out.println("file exists");
		} else {
			System.out.println("file not exists, create it ...");
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	 // 判断文件夹是否存在
	public static void judeDirExists(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				System.out.println("dir exists");
			} else {
				System.out
						.println("the same name file exists, can not create dir");
			}
		} else {
			System.out.println("dir not exists, create it ...");
			file.mkdir();
		}

	}


	/***
	 * 创建报表头列
	 * @param style
	 * @return
	 */
	public static void createExcelHead(String [] header1, XSSFSheet xssfSheet, XSSFCellStyle style){
		XSSFRow headRow = xssfSheet.createRow(0);
		for(int i=0;i<header1.length;i++){
			XSSFCell cell = headRow.createCell(i);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue(header1[i]);
			cell.setCellStyle(style);
		}
	}

	public static XSSFWorkbook createWorkbook(){
		return new XSSFWorkbook();
	}

	public static XSSFSheet createSheet(XSSFWorkbook workbook,String sheetName){
		return workbook.createSheet(sheetName);
	}

	/***
	 * 设置单元格样式 07xlsx
	 * @param workbook
	 * @param groundColor
	 * @return
	 */
	public static XSSFCellStyle createXSSFCellStyle(XSSFWorkbook workbook, short groundColor){
		/**设置字段*/
		XSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short)10);
		font.setFontName("微软雅黑");
		font.setColor((short)8);
		font.setBoldweight(XSSFFont.BOLDWEIGHT_NORMAL);
		/**设置单元格样式*/
		XSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(groundColor);
		style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
		style.setBorderBottom(XSSFCellStyle.BORDER_THICK);
		style.setBorderBottom(XSSFCellStyle.BORDER_THIN);// 下边框
		style.setBorderLeft(XSSFCellStyle.BORDER_THIN);// 左边框
		style.setBorderRight(XSSFCellStyle.BORDER_THIN);// 右边框
		style.setBorderTop(XSSFCellStyle.BORDER_THIN);// 上边框
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 左右居中
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 上下居中
		style.setFont(font);
		return style;
	}

	/**
	 * 加入数据
	 * @param index
	 * @param list
	 * @param sheet
	 * @param style
	 * @throws Exception
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	public static <T extends Object> void addDataXSSF(int index,List<T> list,XSSFSheet sheet,XSSFCellStyle style,String batchNum) {
		/**增添内容数据*/
		for(int i=0;i<list.size();i++){
			T row=list.get(i);
			XSSFRow valueRow = sheet.createRow(index);

			Class[] paramClassArray=new Class[1] ;
			paramClassArray[0]=String.class;
			Method m = null;
			try {
				m = list.get(i).getClass().getMethod("toString", paramClassArray);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			Object[] paramArray= new Object[1];
			paramArray[0]=batchNum;
			String valueStr = "";
			try {
				valueStr = (String) m.invoke(row, paramArray);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			String [] value=valueStr.split(SPLIT_STR);
			for(int j=0;j<value.length;j++){
				XSSFCell cell = valueRow.createCell(j);
				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				if(StringUtils.isEmpty(value[j]))
					cell.setCellValue("-");
				else
					cell.setCellValue(value[j]);
				cell.setCellStyle(style);
			}
			index++;
		}
	}

	public static <T extends Object> int addDataXSSF(int index,Object o,XSSFSheet sheet,XSSFCellStyle style){
		T row = (T) o;
		System.out.println("写入数据:"+row.toString());
		XSSFRow valueRow = sheet.createRow(index);
		Method methods[] = row.getClass().getMethods();
		for (int j=0;j<methods.length;j++) {
			if(methods[j].getName().startsWith("get")){
				XSSFCell cell = valueRow.createCell(j);
				try {
					String valueStr = (String)methods[j].invoke(row,new Object[1]);
					cell.setCellValue(valueStr);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		index++;
		return index;
	}

	public static String createExcel(List result,String path, String fileName, Class excilClass) throws FileNotFoundException {
		Map<String, Object> excilMap = new HashMap<String, Object>();
		excilMap.put(NormalExcelConstants.FILE_NAME, fileName);
		excilMap.put(NormalExcelConstants.CLASS, excilClass);

		excilMap.put(NormalExcelConstants.DATA_LIST, result);
		ExportParams exportParams = new ExportParams();
		exportParams.setType(ExcelType.XSSF);
		excilMap.put(NormalExcelConstants.PARAMS, exportParams);
		Workbook workbook = ExcelUtils.getWorkbook(excilMap);

		//保存Excel
		if (workbook instanceof HSSFWorkbook) {
			fileName += HSSF;
		} else {
			fileName += XSSF;
		}
		ExcelUtils.saveFile(workbook, path, fileName);
		return fileName;
	}
}