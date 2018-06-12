package com.loan_manage.service;

import com.loan_entity.app.NoteResult;
import com.loan_manage.entity.Response;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 机器人接口
 * @author carl.wan
 *2017年9月11日 15:25:25
 */
public interface RobotService {

    /**
     * 发送风控订单
     * @param borrId 合同Id
     * @return
     */
    Response sendRcOrder(Integer borrId) throws Exception;
    /**
     * 风控订单回调结果
     * @param
     * @return
     */
    Response callBackRc(HttpServletRequest request) throws Exception;

    /**
     * 查询百可录订单
     * @param borrNum
     * @return
     */
    Response robotOrderByBorrNum(String borrNum, HttpServletRequest request);
}
