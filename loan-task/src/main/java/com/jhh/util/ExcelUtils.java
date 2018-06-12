package com.jhh.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.params.ExcelExportEntity;
import org.jeecgframework.poi.excel.entity.vo.MapExcelConstants;
import org.jeecgframework.poi.excel.entity.vo.NormalExcelConstants;
import org.jeecgframework.poi.excel.export.ExcelExportServer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

	protected static final String HSSF  = ".xls";
	protected static final String ISO8859_1="ISO8859-1";
    protected static final String UTF8 = "UTF-8";
    protected static final String XSSF  = ".xlsx";
	
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

		}finally {
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

	}
    /**
	 * 将对象属性值转换成Excel文件输出
	 * @param model
	 * @param request
	 * @param response
	 */
	public static void jeecgSingleExcel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response){
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
	
	public static void saveFile(Workbook workbook, String path, String fileName) {
		Assertion.notEmpty(path, "保存路径不能为空");
		Assertion.notEmpty(fileName, "保存文件名不能为空");
		
		//判断文件夹
		File fileDir = new File(path);
		//判断文件
		File file = new File(path + fileName);
		
		judeDirExists(fileDir);
		judeFileExists(file);
		
		// 保存Excel文件
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(file);
			workbook.write(fileOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
	
}