package com.jhh.controller;

import com.jhh.service.BorrowBatchDeductionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by wanzezhong on 2018/4/3.
 */
@Controller
public class TestTeskController {

    @Autowired
    BorrowBatchDeductionsService borrowBatchDeductionsService;

    @RequestMapping(value = "/borrowBatchDeductions/creatFullDeductionsStatus" ,method = RequestMethod.GET)
    public void testCreatFullDeductionsStatus(){
        borrowBatchDeductionsService.creatFullDeductionsStatus();
    }

    @RequestMapping(value = "/borrowBatchDeductions/saveAgriculturalBankStatus" ,method = RequestMethod.GET)
    @ResponseBody
    public String testSaveAgriculturalBankStatus(){
        int count = borrowBatchDeductionsService.saveAgriculturalBankStatus();
        return "成功更新" + count + "条";
    }

    @RequestMapping(value = "/borrowBatchDeductions/saveDeductionsStatus" ,method = RequestMethod.GET)
    @ResponseBody
    public String  testSaveDeductionsStatus(){
        int count = borrowBatchDeductionsService.saveDeductionsStatus();
        return "成功更新" + count + "条";
    }
}
