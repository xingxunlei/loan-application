package com.loan_manage.controller;

import com.loan_api.manager.ManageLoanService;
import com.loan_entity.app.BorrowList;
import com.loan_entity.app.Product;
import com.loan_entity.app.Riewer;
import com.loan_entity.loan.CollectorsLevel;
import com.loan_entity.manager_vo.*;
import com.loan_entity.utils.ManagerResult;
import com.loan_entity.utils.ManagerResultForNet;
import com.loan_manage.service.BpmService;
import com.loan_manage.service.CollectorsLevelService;
import com.loan_manage.service.LoanManagementService;
import com.loan_utils.util.Detect;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.List;


@Controller
@RequestMapping("/manager")
public class ManageLoanController {
    private static Logger log = Logger.getLogger(ManageLoanController.class);
    @Autowired
    private ManageLoanService manageLoanService;
    @Autowired
    private BpmService bpmService;
    @Autowired
    private LoanManagementService loanManagementService;
    @Autowired
    private CollectorsLevelService collectorsLevelService;


    @ResponseBody
    @RequestMapping(value = "/nodeinfo")
    public List<BpmNodeVo> getNodeInfoByPerId(String himid) {
        if (!StringUtils.isNumeric(himid)) {
            return null;
        }
        try {
            return bpmService.getBpmNodesByPerId(himid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/M2001", method = RequestMethod.POST)
    public ManagerResult backPhoneCheckMessage(HttpServletRequest request) {
        StringBuffer info = new StringBuffer();

        InputStream is = null;
        String xing = "";
        try {
            is = request.getInputStream();
            BufferedInputStream buf = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int iRed;
            while ((iRed = buf.read(buffer)) != -1) {
                info.append(new String(buffer, 0, iRed, "UTF-8"));
            }

            xing = URLDecoder.decode(info.toString(), "UTF-8");
            log.info("json:" + xing);
            // System.out.println("zzzzzzzzz" + xing);

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // xing =
        // "{\"phone\":\"18501773154\",\"node_status\":\"0000\",\"node_date\":\"2016-11-04T14:48:25.9202007+08:00\",\"description\":\"通过\"}";
        // System.out.println(xing);
        int str1 = xing.indexOf("{");
        int str2 = xing.indexOf("}") + 1;
        xing = xing.substring(str1, str2);

        JSONObject jsonobject = JSONObject.fromObject(xing);
        ReqBackPhoneCheckVo passport = new ReqBackPhoneCheckVo();
        passport.setPhone(jsonobject.getString("phone"));
        passport.setNode_date(jsonobject.getString("node_date"));
        passport.setDescription(jsonobject.getString("node_status")
                + jsonobject.getString("description"));
        passport.setNode_status(jsonobject.getString("node_status"));
        return manageLoanService.backPhoneCheckMessage(passport);
    }

    @ResponseBody
    @RequestMapping(value = "/M3001", method = RequestMethod.POST)
    public ManagerResultForNet offlineTransfer(HttpServletRequest request) {
        StringBuffer info = new StringBuffer();
        InputStream is = null;
        String xing = "";
        try {
            is = request.getInputStream();
            BufferedInputStream buf = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int iRed;
            while ((iRed = buf.read(buffer)) != -1) {
                info.append(new String(buffer, 0, iRed, "UTF-8"));
            }

            xing = URLDecoder.decode(info.toString(), "UTF-8");
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return manageLoanService.offlineTransfer(xing);
    }

    @ResponseBody
    @RequestMapping(value = "/M300", method = RequestMethod.GET)
    public ManagerResultForNet hh(String hh) {
        return manageLoanService.offlineTransfer(hh);
    }

    @ResponseBody
    @RequestMapping(value = "/personCheckMessage", method = RequestMethod.POST)
    public ManagerResult personCheckMessage(String brroid, String status,
                                            String employ_num, String reason) {
        return manageLoanService.personCheckMessage(brroid, status, employ_num,
                reason);
    }

    @ResponseBody
    @RequestMapping(value = "/transferPersonCheck", method = RequestMethod.POST)
    public ManagerResult transferPersonCheck(String brroid_list, String transfer) {
        return manageLoanService.transferPersonCheck(brroid_list, transfer);
    }

    @ResponseBody
    @RequestMapping(value = "/UpdateBorrowList", method = RequestMethod.POST)
    public ManagerResult UpdateBorrowList(int ispay, String status, int borrid) {
        BorrowList record = new BorrowList();
        record.setId(borrid);
        record.setIspay(ispay);
        record.setBorrStatus(status);
        return manageLoanService.UpdateBorrowList(record);
    }

    @ResponseBody
    @RequestMapping("/M2005")
    public ManagerResult insertRepaymentPlan() {
        return null;
    }

    @ResponseBody
    @RequestMapping("/M2006")
    public ManagerResult UpdateRepaymentPlan() {
        return null;
    }

    @ResponseBody
    @RequestMapping("/M2007")
    public ManagerResult insertOrder() {
        return null;
    }

    @ResponseBody
    @RequestMapping("/M2008")
    public ManagerResult insertBank() {
        return null;
    }

    @ResponseBody
    @RequestMapping("/M2009")
    public ManagerResult UpdateBank() {
        return null;
    }

    @ResponseBody
    @RequestMapping("/M2010")
    public ManagerResult insertProduct() {
        return null;
    }

    @ResponseBody
    @RequestMapping("/M2011")
    public ManagerResult UpdateProduct() {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/getAllProduct", method = RequestMethod.POST)
    public List<Product> getAllProduct() {
        List<Product> productList = manageLoanService.getAllProduct();
        for(Product product : productList){
            if(product.getStatus().equals("1")){
                product.setProductName(product.getProductName() + "(新)");
            }else{
                product.setProductName(product.getProductName() + "(旧)");
            }
        }
        Product pro = new Product();
        pro.setId(0);
        pro.setProductName("全部产品");
        productList.add(pro);
        return productList;
    }

    @ResponseBody
    @RequestMapping("/M2012")
    public ManagerResult insertProductTerm() {
        return null;
    }

    @ResponseBody
    @RequestMapping("/M2013")
    public ManagerResult UpdateProductTerm() {
        return null;
    }

    @ResponseBody
    @RequestMapping("/M2014")
    public ManagerResult insertProductChargeModel() {
        return null;
    }

    @ResponseBody
    @RequestMapping("/M2015")
    public ManagerResult UpdateProductChargeModel() {
        return manageLoanService.UpdateProductChargeModel(null);
    }

    @ResponseBody
    @RequestMapping(value = "/selectRiewerList", method = RequestMethod.POST)
    public List<CollectorsLevel> selectRiewerList(String status) {

        return collectorsLevelService.selectCollectorsLevels();

        //return manageLoanService.selectRiewerList(status);
    }

    @ResponseBody
    @RequestMapping(value = "/selectBorrowLists", method = RequestMethod.POST)
    public String selectBorrowLists(String borrIds, String borrStatus) {
        List<BorrowList> borrowLists = manageLoanService.getBorrList(borrIds, borrStatus);
        if (Detect.notEmpty(borrowLists)) {
            StringBuilder sb = new StringBuilder();
            for (BorrowList borrowList : borrowLists) {
                sb.append(borrowList.getId() + ";");
            }
            return sb.toString();
        }
        return "sucess";
    }

    @ResponseBody
    @RequestMapping(value = "/selectRiewerListAll", method = RequestMethod.POST)
    public List<Riewer> selectRiewerListAll() {
        return manageLoanService.selectRiewerListAll();
    }

    @ResponseBody
    @RequestMapping(value = "/selectUserPrivateVo", method = RequestMethod.POST)
    public PrivateVo selectUserPrivateVo(int himid, HttpServletResponse response) {
        return manageLoanService.selectUserPrivateVo(himid);
    }

    @ResponseBody
    @RequestMapping(value = "/selectLoanInfoPrivateVo", method = RequestMethod.POST)
    public List<LoanInfoVo> selectLoanInfoPrivateVo(int himid) {
        return manageLoanService.selectLoanInfoPrivateVo(himid);
    }

    @ResponseBody
    @RequestMapping(value = "/selectLoanInfoPrivateVoForOperator", method = RequestMethod.POST)
    public List<LoanInfoVo> selectLoanInfoPrivateVoForOperator(int himid) {
        return loanManagementService.selectLoanInfoPrivateVo(himid);
    }

    @ResponseBody
    @RequestMapping(value = "/selectBankInfoVo", method = RequestMethod.POST)
    public List<BankInfoVo> selectBankInfoVo(int himid) {
        return manageLoanService.selectBankInfoVo(himid);
    }

    @ResponseBody
    @RequestMapping(value = "/managerbymanager", method = RequestMethod.POST)
    public ManagerResult managerbymanager(String brroid_list, String status) {
        return manageLoanService.managerbymanager(brroid_list, status);
    }

    @ResponseBody
    @RequestMapping(value = "/getCardPicById", method = RequestMethod.POST)
    public CardPicInfoVo getCardPicById(int himid) {
        return manageLoanService.getCardPicById(himid);
    }

    @ResponseBody
    @RequestMapping(value = "/getRiskReport", method = RequestMethod.POST)
    public ManagerResult getRiskReport(int himid) {
        return manageLoanService.getRiskReport(himid);
    }

    //跑批启动event
    @ResponseBody
    @RequestMapping(value = "/DoDsBatchVo", method = RequestMethod.GET)
    public ManagerResult DoDsBatchVo(String key) {

        if (!"jhhyoumishanjie".equals(key)) {
            return null;
        }
        return manageLoanService.DoDsBatchVo();
    }

    //跑批启动event
    @ResponseBody
    @RequestMapping(value = "/PicBatchVo", method = RequestMethod.GET)
    public ManagerResult PicBatchVo(int pageIndex, int pageSize) {
        return manageLoanService.PicBatchVo(pageIndex, pageSize);
    }

    @ResponseBody
    @RequestMapping(value = "/goBlackList", method = RequestMethod.POST)
    public ManagerResult goBlackList(String himid_list, String blacklist, String reason, String usernum, String type) {
        manageLoanService.goReviewCheck(himid_list, blacklist, reason, usernum, type);
        return manageLoanService.goBlackList(himid_list, blacklist);
    }

    @ResponseBody
    @RequestMapping(value = "/getReviewVoBlackList", method = RequestMethod.POST)
    public List<ReviewVo> getReviewVoBlackList(int himid) {
        return manageLoanService.getReviewVoBlackList(himid);
    }

    /**
     * 。net清结算后回调同步
     *
     * @param key
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/doDsBatchVoback", method = RequestMethod.GET)
    public String doDsBatchVoback(String key) {
        if (!"jhhyoumishanjie".equals(key)) {
            return "-1";
        }
        return manageLoanService.doDsBatchVoback();
    }

    /**
     * 悠米后台查看某人的交易流水
     *
     * @param himid
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getOrders", method = RequestMethod.POST)
    public List getOrders(HttpServletResponse response, String himid) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        return manageLoanService.getOrders(himid);
    }
}
