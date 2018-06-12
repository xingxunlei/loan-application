package com.loan_manage.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.StandardSocketOptions;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class CallBackServlet
 */
@WebServlet("/CallBackServlet")
public class CallBackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	private int maxMemSize = 500 * 1024;
	
    /**
     * Default constructor. 
     */
    public CallBackServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	
		
		long arrivedTimestamp = System.currentTimeMillis();

		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		java.io.PrintWriter responseWriter = response.getWriter();
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// maximum size that will be stored in memory
		factory.setSizeThreshold(maxMemSize);
		// Location to save data that is larger than maxMemSize.
		factory.setRepository(new File("/tmp"));

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		JSONObject responseJson = new JSONObject();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");

		try {
			// Check if request is multi-part
			if (!isMultipart) {
				throw new Exception("3:Request should be multi-part.");
			}

			// Parse the request to get file items.
			List<FileItem> fileItems = upload.parseRequest(request);
			
			JSONObject requestJson = null;
			String temp = null;
			@SuppressWarnings("unused")
			long audioSize = -1;
			byte[] audioByteArray = null;
			String audioFileName = null;

			for (FileItem fi : fileItems) {

				if (fi.isFormField() && fi.getFieldName().equals("work_result")) {

					temp = fi.getString("UTF-8");
					System.out.println("work_result string: " + temp);
				}
				if (fi.isFormField() && fi.getFieldName().equals("token")) {

					temp = fi.getString("UTF-8");
					System.out.println("token string: " + temp);
				}
				if (fi.isFormField() && fi.getFieldName().equals("work_data")) {

					temp = fi.getString("UTF-8");
					System.out.println("workdata string: " + temp);
				}
				if (fi.isFormField() && fi.getFieldName().equals("work_id")) {

					temp = fi.getString("UTF-8");
					System.out.println("work_id string: " + temp);
				}
				
				if (!fi.isFormField() && fi.getFieldName().equals("audio")) {
					audioFileName = fi.getName();
					audioSize = fi.getSize();
					System.out.println(audioSize);
					audioByteArray = fi.get();
				
					createFile("/home/biocloo/tmp/"+audioFileName, audioByteArray);
					
				}

			}

		}catch (Exception e) {
			e.printStackTrace();
			
		}
		
		response.setCharacterEncoding("UTF-8");
		
		responseJson.put("success", "ok");
	
		response.setContentType("application/json");
		responseWriter.println(responseJson.toJSONString());
		responseWriter.close();
		
		
	}

	 public static void createFile(String path, byte[] content) throws IOException {  
		  
	        FileOutputStream fos = new FileOutputStream(path);  

	        fos.write(content);  
	        fos.close();  
	    }  
}
