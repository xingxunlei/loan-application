package com.loan_web.app;

import com.loan_api.contract.ElectronicContractService;
import com.loan_utils.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wanzezhong on 2017/11/23.
 */
@Controller
public class ElectronicContractController {

    private static final Logger logger = LoggerFactory.getLogger(ElectronicContractController.class);

    @Autowired
    ElectronicContractService electronicContractService;

    @ResponseBody
    @RequestMapping(value = "/contract/contract/{borrId}",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getContract(@PathVariable Integer borrId){
        return electronicContractService.createElectronicContract(borrId);
    }

    @ResponseBody
    @RequestMapping(value = "/contract/preview/{borrId}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getContractPreview(@PathVariable Integer borrId){
        return electronicContractService.queryElectronicContract(borrId);
    }

    @ResponseBody
    @RequestMapping(value = "/contract/down/{borrNum}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String downloadContract(@PathVariable String borrNum){
        return electronicContractService.downElectronicContract(borrNum);
    }

    @ResponseBody
    @RequestMapping(value = "/contract/callBack",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String callBack(HttpServletRequest request,String code,String contractNo,String downloadUrl, String isPing){
        //正式数据走业务逻辑
        if(isPing.equals("false")) {
            String productId = request.getParameter("productId");
            if ("".equals(productId)) {
                productId = "10";
            }
            electronicContractService.callBack(code, downloadUrl, contractNo, productId);
        }
        return request.getParameter("code");
    }
}
