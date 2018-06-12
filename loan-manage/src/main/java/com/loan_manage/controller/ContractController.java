package com.loan_manage.controller;

import com.loan_api.contract.ElectronicContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by wanzezhong on 2017/11/27.
 */
@Controller
public class ContractController extends BaseController{

    @Autowired
    ElectronicContractService electronicContractService;

    @ResponseBody
    @RequestMapping(value = "/contract/{borrNum}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getContract(@PathVariable String borrNum){
        return electronicContractService.downElectronicContract(borrNum);
    }
}
