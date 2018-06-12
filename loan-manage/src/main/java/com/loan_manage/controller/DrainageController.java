package com.loan_manage.controller;

import com.loan_api.app.LoanService;
import com.loan_entity.app.NoteResult;
import com.loan_manage.entity.Result;
import com.loan_manage.service.BorrowListService;
import com.loan_manage.service.impl.DrainageServiceImpl;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@Controller
@RequestMapping("/drainage")
public class DrainageController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DrainageServiceImpl drainageService;
    @Autowired
    private BorrowListService borrowListService;
    @Resource(name="noteLoanService")
    private LoanService loanService;
    /**
     * 增加
     * */
    @RequestMapping("/add.action")
    @ResponseBody
    public Result addExecute(HttpServletRequest request){
        Result result = new Result();

        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setRepository(new File(drainageService.getDrainageImageDir()));
            ServletFileUpload upload = new ServletFileUpload(factory);
            Map map = new HashMap(16);
            List<FileItem> fileItems = upload.parseRequest(request);
            FileItem fileItem = null;
            for (FileItem fi : fileItems) {
                //表示文件
                if (!fi.isFormField() && fi.getFieldName().equals("file")) {
                    fileItem = fi;
                }else if(fi.isFormField()){
                    map.put(fi.getFieldName(),fi.getString("UTF-8"));
                }
            }
            int i = 0;
            if(!map.isEmpty()){
                map.put("creation_date",new Date());
                if(fileItem != null){
                    map.put("drainage_img",drainageService.getDrainageImageDir()+fileItem.getName());
                }
                i = drainageService.addDrainage(map);
            }
            if(i>0){
                logger.info("添加产品成功");
                result.setCode(Result.SUCCESS);
                result.setMessage("添加产品成功");
                if(fileItem != null){
                    FileOutputStream fos = new FileOutputStream(drainageService.getDrainageImageDir()+fileItem.getName());
                    fos.write(fileItem.get());
                    fos.close();
                }
            }else{
                result.setCode(Result.FAIL);
                result.setMessage("添加产品失败");
                logger.info("添加产品失败");
            }
            return  result;
        } catch (FileUploadException e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage(e.getMessage());
            return  result;
        }catch (Exception e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage(e.getMessage());
            return  result;
        }
    }
    /**
     * 删除
     * */
    @RequestMapping("/delete.action")
    @ResponseBody
    public Result deleteExecute(String ids,HttpServletRequest request){
        Result result = new Result();
        try {
            int i = 0;
            if(ids != null && ids.length() > 0){
                i = drainageService.deleteDrainage(ids);
            }
            if(i>0){
                logger.info("删除产品成功");
                result.setCode(Result.SUCCESS);
                result.setMessage("删除产品成功");
            }else{
                result.setCode(Result.FAIL);
                result.setMessage("删除产品失败");
                logger.info("删除产品失败");
            }
            return  result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage(e.getMessage());
            return  result;
        }
    }
    @RequestMapping("/update.action")
    @ResponseBody
    public Result updateExecute( HttpServletRequest request){
        Result result = new Result();
        //处理通过form表单形式，上送过来的数据
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setRepository(new File(drainageService.getDrainageImageDir()));
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            Map<String,Object> map = new HashMap(16);
            List<FileItem> fileItems = upload.parseRequest(request);
            FileItem fileItem = null;
            for (FileItem fi : fileItems) {
                //表示文件
                if (!fi.isFormField() && fi.getFieldName().equals("file")) {
                    fileItem = fi;
                //表示其他字段
                }else if(fi.isFormField()){
                    map.put(fi.getFieldName(),fi.getString("UTF-8"));
                }
            }
            int i=0;
            if(!map.isEmpty()){
                map.put("update_date",new Date());
                if(map.get("drainage_status") != null || !"".equals(map.get("drainage_status"))){
                    if(Boolean.valueOf((String)map.get("drainage_status"))){
                        map.put("drainage_status",1);
                    }else{
                        map.put("drainage_status",2);
                    }
                }
                //表示有图片更新
                if(fileItem != null){
                    map.put("drainage_img",drainageService.getDrainageImageDir()+fileItem.getName());
                }
                 i = drainageService.updateDrainage(map);
            }
            if(i>0){
                logger.info("更新产品成功");
                result.setCode(Result.SUCCESS);
                result.setMessage("更新产品成功");
                //产品更新成功之后才保存图片
                if(fileItem != null){
                    FileOutputStream fos = new FileOutputStream(drainageService.getDrainageImageDir()+fileItem.getName());
                    fos.write(fileItem.get());
                    fos.close();
                }
            }else{
                result.setCode(Result.FAIL);
                result.setMessage("更新产品失败");
                logger.info("更新产品失败");
            }
            return  result;
        }catch (FileUploadException e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage(e.getMessage());
            return  result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage(e.getMessage());
            return  result;
        }
    }
    /**
     * 查询所有数据
     * */
    @RequestMapping("/query.action")
    @ResponseBody
    public Result queryExecute(HttpServletRequest request,Map map){
        Result result = new Result();
        try {
            List<Map> list = drainageService.queryDrains(map);
            logger.info("查询成功");
            result.setCode(Result.SUCCESS);
            result.setMessage("查询成功");
            result.setObject(list);
            return  result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage(e.getMessage());
            return  result;
        }
    }
    /**
     * 根据id查询明细
     * */
    @RequestMapping("/query/{id}.action")
    @ResponseBody
    public Result queryByIdExecute(@PathVariable String id, HttpServletRequest request){
        Result result = new Result();
        try {
            Map m = (Map)drainageService.queryById(id);
            logger.info("查询成功");
            result.setCode(Result.SUCCESS);
            result.setMessage("查询成功");
            result.setObject(m);
            return  result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage(e.getMessage());
            return  result;
        }
    }

    /**
     * 查询最新合同信息
     * */
    @RequestMapping("/query/borrow/{id}.action")
    @ResponseBody
    public Result queryLastBorrow(@PathVariable String id, HttpServletRequest request){
        Result result = new Result();
        try {
            Map m = (Map)borrowListService.queryLastBorrowList(Integer.valueOf(id));
            result.setCode(Result.SUCCESS);
            result.setMessage("查询成功");
            if(m != null && !m.isEmpty()){
                //表明是人工审核直接返回结果
                if("BS001".equals(m.get("borr_status")) && m.get("id") != null){
                    result.setObject(m);
                    return  result;
                }else if("BS001".equals(m.get("borr_status"))){
                    //查询认证节点
                    NoteResult noteResult = loanService.checkBpm(id);
                    m.put("code",noteResult.getCode());
                    m.put("data",noteResult.getData());
                    result.setObject(m);
                    return  result;
                }
            }
            logger.info("查询成功");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Result.FAIL);
            result.setMessage(e.getMessage());
            return  result;
        }
    }
}
