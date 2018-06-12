package com.loan_manage.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.loan_manage.entity.Response;
import com.loan_manage.service.RobotService;
import com.loan_utils.util.Detect;
import com.loan_utils.util.HttpUtils;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 机器人入口
 * carl.wan
 * 2017年9月12日 09:54:52
 */
@Controller
public class RobotController extends BaseController{

    private static final Logger logger = LoggerFactory.getLogger(RobotController.class);
	@Autowired
	private RobotService robotService;



	@RequestMapping(value = "/robot/rcOrder" ,method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public Response sendOrder(Integer borrId) throws Exception {
		logger.info("/robot/rcOrder begin" + borrId);
//		Response response = robotService.sendRcOrder(borrId);
//		return  response;
		//2017年11月29日 10:44:33  YMOLBUG-83	【悠米生产环境】【风控百可录电话关停】
		return null;
	}

	@RequestMapping(value = "/robot/callBack/rcOrder" ,method = RequestMethod.POST, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public String callBackRcOrder(HttpServletRequest request) throws Exception {
		logger.info("callBackRcOrder start");
		Response response = robotService.callBackRc(request);
		if(response != null && response.getData() != null){
            return response.getData().toString();
        }
        logger.info("callBackRcOrder end" + JSONObject.toJSONString(response));
        return JSONObject.toJSONString(response);

	}

	@RequestMapping(value = "/robotOrder/{borrNum}" ,method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public Response queryRobotOrder(HttpServletRequest request, @PathVariable String borrNum) throws Exception {

		return robotService.robotOrderByBorrNum(borrNum, request);
	}

	public static void main(String arge[]) throws Exception {
		String url = "http://localhost:8092/zloan-manage/robot/rcOrder.action";
		Map map = new HashMap<>();
		map.put("borrId","123");
		HttpUtils.sendPost(url, HttpUtils.toParam(map));
//		HttpUtils.sendPost(url, JSONObject.toJSONString(map),"x-www-form-urlencoded");
	}
}
