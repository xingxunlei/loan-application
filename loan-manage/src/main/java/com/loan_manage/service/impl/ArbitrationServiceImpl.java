package com.loan_manage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.loan_api.contract.ElectronicContractService;
import com.loan_entity.contract.Contract;
import com.loan_manage.entity.arbitration.*;
import com.loan_manage.exception.ArbitrationException;
import com.loan_manage.mapper.ArbitrationMapper;
import com.loan_manage.service.ArbitrationService;
import com.loan_manage.utils.FileUtils;
import com.loan_manage.utils.PdfUtils;
import com.loan_manage.utils.ZipUtils;
import com.loan_utils.util.DateUtil;
import com.loan_utils.util.ExcelUtils;
import com.loan_utils.util.HttpUrlPost;
import com.loan_utils.util.PropertiesReaderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Create by wanzezhong on
 * 2018年3月28日 15:01:50
 */
@Slf4j
@Service
public class ArbitrationServiceImpl implements ArbitrationService {

    @Autowired
    ElectronicContractService electronicContractService;
    @Autowired
    private ArbitrationMapper arbitrationMapper;

    private static final String FILE_SAVE_DIR = PropertiesReaderUtil.read("system", "zipDir");
    private static final String MATERIAL_DIR = PropertiesReaderUtil.read("system", "materialDir");
    private static final String SEAL_SAVE_PATH = PropertiesReaderUtil.read("system", "sealPath");
    private static final String ARBITRATION_ORDER_PATH = PropertiesReaderUtil.read("system", "arbitrationOrderPath");
    private static final String PAYMENT_VOUCHER_PATH = PropertiesReaderUtil.read("system", "paymentVoucherPath");

    public static final String PDF_COLLECTION_RECORD = "cuishoujilu.pdf";
    public static final String PDF_REPAY_RECORD = "huankuanjilu.pdf";
    public static final String PAYMENT_VOUCHER = "zhifupingzheng.pdf";
    public static final String BORROW_AGREEMENT = "jiekuanxieyi.pdf";
    public static final String AUTH_ENTRUSTMENT_NAME = "qianzhangshouquanshu.pdf";

    private static final String COMPANY_NAME = "上海米缸企业信用征信有限公司";
    private static final String EXTEND_FILE = "extFile.json";
    private static final String JPG_CARD_Z = "被申请人%s身份证正面.jpg";
    private static final String JPG_CARD_F = "被申请人%s身份证反面.jpg";
    private static final String ZIP_FILE_NAME = "%s%s.s.zip";
    private static final String EXCEL_FILE_NAME = "%s.se";

    private static final String EXCEL_MATTER = "1、被申请人向申请人一支付未还本金及利息共计{suplusTotal}元（借款本金{loanMoney}元，已还{repayTotal}元，自{payDate}起至{repaymentDate}止共{loanDays}天，按日利率{dailyRate}%计算，利息为{interest}元）。\n" +
            "2、被申请人向申请人一支付逾期利息及逾期违约金（逾期利息暂计算自{overdueDate}起至{now}止共{overdueDays}天，并计算至实际付清之日止，以{loanMoney}元为基数，按日利率{overdueDailyRate}%计算，逾期利息为{overdueInterest}元；逾期违约金以{loanMoney}为基数按{liquidatedDamagesRate}%计算，逾期违约金为{liquidatedDamages}元），可以{loanMoney}元为基数，自{overdueDate}至实际偿还之日，按年化24%计算。\n" +
            "3、被申请人向申请人二支付平台管理费及快速信审费{serviceFeeTotal}元（平台管理费{serviceFeeManage}元，快速信审费{serviceFeeTrust}元，优惠{serviceFeeDiscount}元）。\n" +
            "4、被申请人承担本案仲裁费。";
    private static final String EXCEL_REASONS = "“悠米闪借”APP系为申请人二为被申请人提供借款咨询、居间、账户管理等服务的网络平台。\n" +
            "{registrationDate}，被申请人{borrowerName}自行在“悠米闪借”平台注册用户，并于{loanDate}在网络平台上同意并确认《借款协议》，双方约定：甲方（申请人一）同意向乙方（被申请人）提供总额度为人民币{loanMoney}元整的借款额度，{loanDays}天归还借款本金和利息，日利息按照{dailyRate}%即人民币{interest}元计算；乙方（被申请人）在借款成功后向丙方（申请人二）支付平台管理费及快速信审费{serviceFeeActual}元优惠{serviceFeeDiscount}元即{serviceFeeTotal}元；若乙方（被申请人）逾期还款，逾期利息=借款本金*利率{overdueDailyRate}%*逾期天数，逾期违约金=借款金额*{liquidatedDamagesRate}%。 \n" +
            "{loanDate}，借款人（被申请人）{borrowerName}向出借人（申请人一）方伟借款{loanMoney}元，借期{loanDays}天，从{payDate}至{repaymentDate}（24：00前，节假日不顺延）。出借人（申请人一）方伟确认上海米缸企业信用征信有限公司对借款人（被申请人）的身份信息审核无误后，出借人（申请人一）方伟通过“悠米闪借”网络平台，向{borrowerName}（被申请人）放款{loanMoney}元。\n" +
            "按照借款协议约定，被申请人应当于{repaymentDate}向申请人一归还借款本金{loanMoney}元，利息{interest}元、向申请人二支付平台管理费及快速信审费{serviceFeeTotal}元。但是，约定的还款期限届满后，被申请人只归还本金{repayTotal}元，拒绝归还剩余本金和其他费用。根据《借款协议》第十二条第1款约定：“双方因本合同引起的或与本合同有关的争议，均同意提请中国广州仲裁委员会, 按照申请仲裁时该会现行有效的网络仲裁规则进行网络仲裁并书面审理案件。仲裁裁决是终局的，对双方均有约束力”。\n" +
            "基于上述事实，为维护申请人一、申请人二合法权益，根据合同中约定的仲裁条款，特申请广州仲裁委员会予以仲裁。";

    private static final String CARD_PERSON_URL = "http://cas.youmishanjie.com/zloan-manage/manager/getCardPicById.action";

    private static final int FILES_NUM = 9;
    private static final int CONTRACT_STATUS_1 = 1;
    private static final int CONTRACT_STATUS_2 = 2;
    private static final int CONTRACT_STATUS_3 = 3;
    private static final String JSON_RETURN_CODE_10000 = "10000";
    private static final String JSON_RETURN_CODE_KEY = "code";
    private static final String JSON_RETURN_DATA_KEY = "data";

    private static Map<String, String[]> PAYMENT_VOUCHER_MAP;
    private static Set<String> PAYMENT_VOUCHER_MAP_KEYSET;
    private static List<String> NO_DS_LIST;

    static {
        PAYMENT_VOUCHER_MAP = new HashMap<>();
        List<String> datas = FileUtils.readFileByLine(ARBITRATION_ORDER_PATH);
        if (datas != null && datas.size() > 0) {
            datas.forEach(s -> {
                String[] contents = s.split(",");
                PAYMENT_VOUCHER_MAP.put(contents[0], contents);
            });
        }

        PAYMENT_VOUCHER_MAP_KEYSET = PAYMENT_VOUCHER_MAP.keySet();
        NO_DS_LIST = FileUtils.readFileByLine("D:/orderMapRemove.txt");
    }

    @Override
    public List<Map<String, String>> transferArbitration(String perId, String borrId) {
        List<Map<String, String>> list = getBorrList(perId, borrId);

        List<Map<String, String>> results = new ArrayList<>();
        int count = 0;
        for (Map<String, String> map : list) {
            String fileName;
            try {
                fileName = preparationMaterials(map.get("perId"), map.get("borrId"));
                map.put("message", fileName);
                map.put("code", "SUCCESS");
                count ++;
            } catch (ArbitrationException e) {
                map.put("message", e.getMessage());
                map.put("code", "FAIL");
            }

            results.add(map);

            if (count > 1000) {
                break;
            }
        }
        return results;
    }

    /**
     * 仲裁数据-数据列表
     */
    private List<Map<String, String>> getBorrList(String perId, String borrId) {
        List<Map<String, String>> list = new ArrayList<>();
        if (!(StringUtils.isEmpty(perId) && StringUtils.isEmpty(borrId))) {
            Map<String, String> map = new HashMap<>(16);
            map.put("borrId", borrId);
            map.put("perId", perId);
            list.add(map);

            return list;
        }

        PAYMENT_VOUCHER_MAP.forEach((k, v) -> {
            if (NO_DS_LIST.contains(k)) {
                return;
            }

            Map<String, String> map = new HashMap<>(16);
            map.put("borrId", k);
            map.put("perId", v[4]);
            list.add(map);
        });
        return list;
    }

    /**
     * 仲裁数据-准备材料
     */
    private String preparationMaterials(String perId, String borrId) {
        try {
            // 生成签章授权书
            generateAuth(borrId);
            // 生成借款协议
            generateBorrowAgreement(borrId);

            // 身份证照片
            getIdCard(borrId, perId);

            // 催收记录
            getCollectionRecords(borrId, perId);

            // 还款记录
            getRepayRecords(borrId, perId);

            // 打款凭证
            getPaymentVoucher(borrId, perId);

            // Excel模版
            getTemplateExcl(borrId, perId);

            // Json模版
            getJsonFile(borrId, perId);

            // 下载签章授权书
            getAuthEntrustment(borrId, perId);
            // 下载借款协议
            getBorrowAgreement(borrId, perId);

            // 检查相关材料是否齐全
            checkFiles(borrId, perId);

            return zipArbitrationFile(borrId, perId);
        }catch (Exception e) {
            throw new ArbitrationException(e);
        }
    }

    /**
     * 仲裁数据-签章授权书（电子签章）
     */
    private void generateAuth(String borrId) {
        String contract = electronicContractService.createElectronicContract(Integer.parseInt(borrId), "13");
        if (StringUtils.isEmpty(contract)) {
            log.info("generateAuth【"+ borrId +"】-------电子合同接口调用失败---签章授权书返回结果为空------");
            throw new ArbitrationException("generateAuth【"+ borrId +"】-------电子合同接口调用失败---签章授权书返回结果为空------");
        }

        JSONObject result = JSONObject.parseObject(contract);
        JSONObject data = result.getJSONObject(JSON_RETURN_DATA_KEY);
        if (!JSON_RETURN_CODE_10000.equals(data.getString(JSON_RETURN_CODE_KEY))) {
            log.info("generateAuth【"+ borrId +"】-------电子合同接口调用失败---签章授权书返回结果【"+ data.toString() +"】------");
            throw new ArbitrationException("generateAuth【"+ borrId +"】-------电子合同接口调用失败---签章授权书返回结果【"+ data.toString() +"】------");
        }
    }

    /**
     * 仲裁数据-电子合同（借款协议-电子签章）
     */
    private void generateBorrowAgreement(String borrId) {
        String contract = electronicContractService.createElectronicContract(Integer.parseInt(borrId), "12");
        if (StringUtils.isEmpty(contract)) {
            log.info("generateBorrowAgreement【"+ borrId +"】-------电子合同接口调用失败---借款协议返回结果为空------");
            throw new ArbitrationException("generateBorrowAgreement【"+ borrId +"】-------电子合同接口调用失败---借款协议返回结果为空------");
        }

        JSONObject result = JSONObject.parseObject(contract);
        JSONObject data = result.getJSONObject(JSON_RETURN_DATA_KEY);
        if (!JSON_RETURN_CODE_10000.equals(data.getString(JSON_RETURN_CODE_KEY))) {
            log.info("generateBorrowAgreement【"+ borrId +"】-------电子合同接口调用失败---签章授权书返回结果【"+ data.toString() +"】------");
            throw new ArbitrationException("generateBorrowAgreement【"+ borrId +"】-------电子合同接口调用失败---借款协议返回结果【"+ data.toString() +"】------");
        }
    }

    /**
     * 准备材料-被申请人身份证
     */
    private void getIdCard(String borrId, String perId) {
        Map<String, String> map = arbitrationMapper.queryCardInfoById(perId);
        if (map == null || map.size() < 1) {
            log.info("getIdCard【"+ perId +"】-------未查询到人员身份证信息---------");
            throw new ArbitrationException("未查询到人员身份证信息----perId:" + perId);
        }

        Map<String, String> paraMap = new HashMap<>(16);
        paraMap.put("himid", perId);
        String text = HttpUrlPost.sendPost(CARD_PERSON_URL, paraMap);
        if (StringUtils.isEmpty(text)) {
            log.info("getIdCard【"+ perId +"】-------未获取到相关身份证信息---返回数据为空------");
            throw new ArbitrationException("未获取到相关身份证信息----perId:" + perId);
        }

        JSONObject jsonObject = JSONObject.parseObject(text);
        if (jsonObject == null) {
            log.info("getIdCard【"+ perId +"】-------未获取到相关身份证信息---转json异常------");
            throw new ArbitrationException("未获取到相关身份证信息----perId:" + perId);
        }

        String imageZ = jsonObject.getString("imageZ");
        if (StringUtils.isEmpty(imageZ)) {
            log.info("getIdCard【"+ perId +"】-------身份证反面信息异常【"+ imageZ +"】------");
            throw new ArbitrationException("未获取到相关身份证信息----perId:" + perId);
        }
        FileUtils.generateImage(imageZ, getMaterialPath(borrId, perId) + String.format(JPG_CARD_F, map.get("perName")));

        String imageF = jsonObject.getString("imageF");
        if (StringUtils.isEmpty(imageF)) {
            log.info("getIdCard【"+ perId +"】-------身份证反面信息异常【"+ imageF +"】------");
            throw new ArbitrationException("未获取到相关身份证信息----perId:" + perId);
        }
        FileUtils.generateImage(imageF, getMaterialPath(borrId, perId) + String.format(JPG_CARD_Z, map.get("perName")));

    }

    /**
     * 准备材料-催收记录
     */
    private void getCollectionRecords(String borrId, String perId) {
        List<Map<String, Object>> list = arbitrationMapper.selectRemarkInfo(borrId);
        if (list.size() < 1) {
            log.info("getCollectionRecords【"+ borrId +","+ perId +"】-------准备材料-催收记录出现错误 未查询到对用户的催收记录---------");
            throw new ArbitrationException("未查询到相关催收记录----perId:" + perId + "  borrId:" + borrId);
        }

        String[] tableHeaderName = new String[]{"合同编号", "姓名", "身份证号", "手机号", "催收时间", "催收备注", "催收人", "催收方式", "去电电话"};
        String[] tableBodyName = new String[]{"borrNum", "customerName", "customerCard", "customerPhone", "collectionTime", "collectionResult", "collectionUser", "dunningType", "dunningTocall"};
        try {
            PdfUtils.generateTablePDF(list, tableHeaderName, tableBodyName, getMaterialPath(borrId, perId) + PDF_COLLECTION_RECORD, COMPANY_NAME, SEAL_SAVE_PATH, true);
        } catch (Exception e) {
            log.info("getCollectionRecords【"+ borrId +","+ perId +"】-------准备材料-催收记录出现错误---------");
            throw new ArbitrationException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 催收材料-还款记录
     */
    private void getRepayRecords(String borrId, String perId) {
        List<Map<String, Object>> list = arbitrationMapper.selectRepayRecords(borrId);
        String[] tableHeaderName = new String[]{"合同编号", "姓名", "身份证号", "手机号", "开户行", "银行卡号", "还款类型", "还款状态", "还款金额", "还款流水", "还款时间"};
        String[] tableBodyName = new String[]{"borrNum", "customerName", "customerCard", "customerPhone", "bankName", "bankCardNum", "repayTypeName", "repayStatusName", "repayAmount", "orderCode", "repayTime"};
        try {
            PdfUtils.generateTablePDF(list, tableHeaderName, tableBodyName, getMaterialPath(borrId, perId) + PDF_REPAY_RECORD, COMPANY_NAME, SEAL_SAVE_PATH, true);
        } catch (Exception e) {
            log.info("getRepayRecords【"+ borrId +","+ perId +"】-------准备材料-还款记录出现错误---------");
            throw new ArbitrationException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 准备材料-支付凭证
     */
    private void getPaymentVoucher(String borrId, String perId) {
        if (!PAYMENT_VOUCHER_MAP_KEYSET.contains(borrId)) {
            log.info("getPaymentVoucher【"+ borrId +","+ perId +"】-------未查询到相关支付凭证---------");
            throw new ArbitrationException("未查询到相关支付凭证----perId:" + perId + "  borrId:" + borrId);
        }

        String[] paymentVoucherContent = PAYMENT_VOUCHER_MAP.get(borrId);
        try {
            FileUtils.copyFile(PAYMENT_VOUCHER_PATH.endsWith(File.separator) ? PAYMENT_VOUCHER_PATH + paymentVoucherContent[3] : PAYMENT_VOUCHER_PATH + File.separator  + paymentVoucherContent[3], getMaterialPath(borrId, perId) + PAYMENT_VOUCHER);
        } catch (Exception e) {
            log.info("getPaymentVoucher【"+ borrId +","+ perId +"】-------准备材料-支付凭证出现错误---------");
            throw new ArbitrationException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 准备材料-签章委托书
     */
    private void getAuthEntrustment(String borrId, String perId) {
        Contract contract = new Contract();
        contract.setProductId("13");
        contract.setBorrId(Integer.parseInt(borrId));
        contract = arbitrationMapper.selectContract(contract);
        if (contract == null || contract.getStatus() == CONTRACT_STATUS_1) {
            log.info("getAuthEntrustment【"+ borrId +","+ perId +"】-------准备材料-签章委托书尚未生成 1s 后重新获取---------");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getAuthEntrustment(borrId, perId);
            return;
        }

        if (contract.getStatus() == CONTRACT_STATUS_2) {
            log.info("getAuthEntrustment【"+ borrId +","+ perId +"】-------准备材料-签章委托书生成中 1s 后重新获取---------");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getAuthEntrustment(borrId, perId);
            return;
        }

        if (contract.getStatus() != CONTRACT_STATUS_3) {
            log.info("getAuthEntrustment【"+ borrId +","+ perId +"】-------准备材料-签章委托书生成失败---------");
            throw new ArbitrationException("getAuthEntrustment【"+ borrId +","+ perId +"】-------准备材料-签章委托书生成失败---------");
        }

        if (StringUtils.isEmpty(contract.getContractUrl())) {
            log.info("getAuthEntrustment【"+ borrId +","+ perId +"】-------准备材料-签章委托书生成中 1s 后重新获取---------");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getAuthEntrustment(borrId, perId);
            return;
        }

        try {
            FileUtils.downloadFile(contract.getContractUrl(), getMaterialPath(borrId, perId) + AUTH_ENTRUSTMENT_NAME);
        } catch (Exception e) {
            log.info("getAuthEntrustment【"+ borrId +","+ perId +"】-------准备材料-下载签章委托书出现错误---------");
            throw new ArbitrationException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 准备材料-借款协议
     */
    private void getBorrowAgreement(String borrId, String perId) {
        Contract contract = new Contract();
        contract.setProductId("12");
        contract.setBorrId(Integer.parseInt(borrId));
        contract = arbitrationMapper.selectContract(contract);
        if (contract == null || contract.getStatus() == CONTRACT_STATUS_1) {
            log.info("getBorrowAgreement【"+ borrId +","+ perId +"】-------准备材料-借款协议尚未生成 1s 后重新获取---------");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getBorrowAgreement(borrId, perId);
            return;
        }

        if (contract.getStatus() == CONTRACT_STATUS_2) {
            log.info("getBorrowAgreement【"+ borrId +","+ perId +"】-------准备材料-借款协议生成中 1s 后重新获取---------");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getBorrowAgreement(borrId, perId);
            return;
        }

        if (contract.getStatus() != CONTRACT_STATUS_3) {
            log.info("getBorrowAgreement【"+ borrId +","+ perId +"】-------准备材料-借款协议生成失败---------");
            throw new ArbitrationException("getBorrowAgreement【"+ borrId +","+ perId +"】-------准备材料-借款协议生成失败---------");
        }

        try {
            FileUtils.downloadFile(contract.getContractUrl(), getMaterialPath(borrId, perId) + BORROW_AGREEMENT);
        } catch (Exception e) {
            log.info("getBorrowAgreement【"+ borrId +","+ perId +"】-------准备材料-下载借款协议出现错误---------");
            throw new ArbitrationException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 准备材料-excel模板
     */
    private void getTemplateExcl(String borrId, String perId) {
        List<TemplateExcl> result = new ArrayList<>();
        result.add(handleLineOne(arbitrationMapper.selectTemplateExclOneLine(Integer.parseInt(borrId)), borrId));
        result.add(TemplateExcl.assembleData());
        result.add(handleCardName(arbitrationMapper.selectTemplateExclThreeLine(Integer.parseInt(borrId))));
        result.add(TemplateExcl.assembleData("证据文件名","200字内简单描述证据文件所证明的事实"));
        result.add(TemplateExcl.assembleData(EXTEND_FILE,"扩展项文件"));
        result.add(TemplateExcl.assembleData(BORROW_AGREEMENT,"借款协议"));
        result.add(TemplateExcl.assembleData(PAYMENT_VOUCHER,"支付凭证"));
        result.add(TemplateExcl.assembleData(PDF_REPAY_RECORD,"还款记录"));
        result.add(TemplateExcl.assembleData(PDF_COLLECTION_RECORD,"催收记录"));
        result.add(TemplateExcl.assembleData(AUTH_ENTRUSTMENT_NAME,"签章授权书"));
        try {
            ExcelUtils.createExcel(result, getMaterialPath(borrId, perId), String.format(EXCEL_FILE_NAME, result.get(2).getMoney() + DateUtil.getDateStringyyyymmdd(new Date())), TemplateExcl.class);
        } catch (Exception e) {
            log.info("getTemplateExcl【"+ borrId +","+ perId +"】-------准备材料-Excel模板出现错误---------");
            throw new ArbitrationException(e);
        }
    }

    /**
     * 准备材料-json文件
     */
    private void getJsonFile(String borrId, String perId) {
        TemplateJson templateJson = new TemplateJson();
        templateJson.setClaim(getClaim(Integer.parseInt(borrId)));
        templateJson.setEvidences(getEvidences(Integer.parseInt(borrId)));
        templateJson.setPrdsNum("13");
        JSONObject jsonObject = (JSONObject) JSONObject.parse(JSONObject.toJSON(templateJson).toString());
        File file = new File(getMaterialPath(borrId, perId) + EXTEND_FILE);
        if (!file.exists()) {
            try {
                boolean result = file.createNewFile();
                if (!result) {
                    log.info("getJsonFile【"+ borrId +","+ perId +"】-------准备材料-JSON文件出现错误 文件创建失败---------");
                    throw new ArbitrationException("getJsonFile【"+ borrId +","+ perId +"】-------准备材料-JSON文件出现错误 文件创建失败---------");
                }
            } catch (Exception e) {
                log.info("getJsonFile【"+ borrId +","+ perId +"】-------准备材料-JSON文件出现错误---------");
                throw new ArbitrationException(e.getMessage(), e.getCause());
            }
        }

        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.info("getJsonFile【"+ borrId +","+ perId +"】-------准备材料-JSON文件出现错误---------");
            throw new ArbitrationException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 打包检查-检查材料是否齐全
     */
    private void checkFiles(String borrId, String perId) {
        List<String> list = FileUtils.listFile(getMaterialPath(borrId, perId));

        list.forEach(log::info);
        log.info("checkFiles【"+ borrId +","+ perId +"】-------检查材料-文件数量【"+ list.size() +"】---------");
        if (list.size() < FILES_NUM) {
            log.info("checkFiles【"+ borrId +","+ perId +"】-------检查材料-文件数量有误---------");
            preparationMaterials(borrId, perId);
        }

    }

    /**
     * 打包材料-调用仲裁接口
     */
    private String zipArbitrationFile(String borrId, String perId) {
        String zipName = getZipFilePath() + String.format(ZIP_FILE_NAME, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), perId);
        try {
            ZipUtils.compress(false, zipName, getMaterialPath(borrId, perId) );
            return zipName;
        } catch (Exception e) {
            log.info("zipArbitrationFile【"+ perId +"】-------打包材料出现错误---------");
            throw new ArbitrationException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 获取准备材料存放路径
     */
    private String getMaterialPath(String borrId, String perId) {
        String path = String.format(MATERIAL_DIR, perId, borrId);
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        return path;
    }

    /**
     * 获取压缩包存放路径
     */
    private String getZipFilePath() {
        String path = FILE_SAVE_DIR;
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        return path;
    }

    /**
     * 处理excel模板中被申请人身份证照片名称
     */
    private TemplateExcl handleCardName(TemplateExcl templateExcl) {
        templateExcl.setArbitrationCourt(String.format(JPG_CARD_Z, templateExcl.getMoney()));
        templateExcl.setBusinessNum(String.format(JPG_CARD_F, templateExcl.getMoney()));
        return templateExcl;
    }

    /**
     * 处理excel模版中的请求事项、事实与理由、请求标的金额字段
     */
    private TemplateExcl handleLineOne(TemplateExcl templateExcl, String borrId) {
        ExtData extData = getExtData(Integer.parseInt(borrId));

        templateExcl.setRequestItems(EXCEL_MATTER.replaceAll("\\{suplusTotal}", String.format("%.2f", Float.parseFloat(extData.getLoanContractMoney()) - Float.parseFloat(extData.getRepayMoneyTotal()) + Float.parseFloat(extData.getAccrual()))).replaceAll("\\{loanMoney}", extData.getLoanContractMoney()).replaceAll("\\{repayTotal}", extData.getRepayMoneyTotal()).replaceAll("\\{payDate}", extData.getPayDate()).replaceAll("\\{repaymentDate}", extData.getRepaymentDate()).replaceAll("\\{loanDays}", extData.getLoanDays()).replaceAll("\\{dailyRate}", extData.getDailyRate()).replaceAll("\\{interest}", extData.getAccrual()).replaceAll("\\{overdueDate}", LocalDate.parse(extData.getRepaymentDate()).plusDays(1L).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).replaceAll("\\{now}", extData.getAccountingDate()).replaceAll("\\{overdueDays}", extData.getAccountingDays()).replaceAll("\\{overdueDailyRate}", extData.getLiquidatedDamages1DailyRate()).replaceAll("\\{overdueInterest}", extData.getLiquidatedDamages1()).replaceAll("\\{liquidatedDamagesRate}", extData.getLiquidatedDamages2Rate()).replaceAll("\\{liquidatedDamages}", extData.getPenalty()).replaceAll("\\{serviceFeeTotal}", String.format("%.2f", Double.parseDouble(extData.getServiceFee1()) + Double.parseDouble(extData.getServiceFee2()) - Double.parseDouble(extData.getServiceFeeDiscount()))).replaceAll("\\{serviceFeeManage}", extData.getServiceFee1()).replaceAll("\\{serviceFeeTrust}", extData.getServiceFee2()).replaceAll("\\{serviceFeeDiscount}", extData.getServiceFeeDiscount()));

        templateExcl.setReasons(EXCEL_REASONS.replaceAll("\\{registrationDate}", extData.getRegistrationDate()).replaceAll("\\{borrowerName}", extData.getBorrowerName()).replaceAll("\\{loanDate}", extData.getLoanDate()).replaceAll("\\{loanMoney}", extData.getLoanContractMoney()).replaceAll("\\{loanDays}", extData.getLoanDays()).replaceAll("\\{dailyRate}", extData.getDailyRate()).replaceAll("\\{interest}", extData.getAccrual()).replaceAll("\\{serviceFeeActual}", String.format("%.2f", Double.parseDouble(extData.getServiceFee1()) + Double.parseDouble(extData.getServiceFee2()))).replaceAll("\\{serviceFeeDiscount}", String.format("%.2f", Double.parseDouble(extData.getServiceFeeDiscount()))).replaceAll("\\{serviceFeeTotal}", String.format("%.2f", Double.parseDouble(extData.getServiceFee1()) + Double.parseDouble(extData.getServiceFee2()) - Double.parseDouble(extData.getServiceFeeDiscount()))).replaceAll("\\{overdueDailyRate}", extData.getLiquidatedDamages1DailyRate()).replaceAll("\\{liquidatedDamagesRate}", extData.getLiquidatedDamages2Rate()).replaceAll("\\{payDate}", extData.getPayDate()).replaceAll("\\{repaymentDate}", extData.getRepaymentDate()).replaceAll("\\{repayTotal}", extData.getRepayMoneyTotal()));

        templateExcl.setBorrowMoney(getExcelDisputeFee(extData));

        return templateExcl;
    }

    /**
     * 处理json文件中的仲裁申请内容
     */
    private Claim getClaim(Integer borrId){
        ExtData extData = getExtData(borrId);
        if (null == extData) {
            return null;
        }

        Claim claim = new Claim();
        claim.setReason(EXCEL_REASONS.replaceAll("\\{registrationDate}", extData.getRegistrationDate()).replaceAll("\\{borrowerName}", extData.getBorrowerName()).replaceAll("\\{loanDate}", extData.getLoanDate()).replaceAll("\\{loanMoney}", extData.getLoanContractMoney()).replaceAll("\\{loanDays}", extData.getLoanDays()).replaceAll("\\{dailyRate}", extData.getDailyRate()).replaceAll("\\{interest}", extData.getAccrual()).replaceAll("\\{serviceFeeActual}", String.format("%.2f", Double.parseDouble(extData.getServiceFee1()) + Double.parseDouble(extData.getServiceFee2()))).replaceAll("\\{serviceFeeDiscount}", String.format("%.2f", Double.parseDouble(extData.getServiceFeeDiscount()))).replaceAll("\\{serviceFeeTotal}", String.format("%.2f", Double.parseDouble(extData.getServiceFee1()) + Double.parseDouble(extData.getServiceFee2()) - Double.parseDouble(extData.getServiceFeeDiscount()))).replaceAll("\\{overdueDailyRate}", extData.getLiquidatedDamages1DailyRate()).replaceAll("\\{liquidatedDamagesRate}", extData.getLiquidatedDamages2Rate()).replaceAll("\\{payDate}", extData.getPayDate()).replaceAll("\\{repaymentDate}", extData.getRepaymentDate()).replaceAll("\\{repayTotal}", extData.getRepayMoneyTotal()));
        claim.setFiles(Files.getFiles(extData.getBorrowerName() + "借贷纠纷案仲裁申请书.docx"));
        claim.setExtData(ExtData.getExtData(extData));
        claim.setClaimItems(getClaimItems(extData));
        return claim;
    }

    /**
     * 处理json文件中的扩展字段信息
     */
    private ExtData getExtData(Integer borrId){
        return arbitrationMapper.selectExtDataByBorrId(borrId);
    }

    /**
     * 处理json文件中的诉求项
     */
    private List<ClaimItems> getClaimItems(ExtData extData){
        if (null == extData) {
            return null;
        }

        List<ClaimItems> claimItemsList = new ArrayList<>();
        claimItemsList.add(ClaimItems.getClaimItems1(extData));
        claimItemsList.add(ClaimItems.getClaimItems2(extData));
        claimItemsList.add(ClaimItems.getClaimItems3(extData));
        claimItemsList.add(ClaimItems.getClaimItems4());
        return claimItemsList;
    }

    /**
     * 处理json文件中的证据项
     */
    private List<Evidences> getEvidences(Integer borrId){
        EvidenceItemsExtData evidenceItem = arbitrationMapper.selectEvidenceItemsExtDataByBorrId(borrId);
        if (null == evidenceItem) {
            return null;
        }

        Evidences evidences = new Evidences();
        evidences.setProvider("方伟");
        evidences.setProviderType("申请方");

        List<EvidenceItems> evidenceItems = new ArrayList<>();

        //借款协议
        evidenceItems.add(EvidenceItems.getEvidenceItems1(evidenceItem));

        //支付凭证
        evidenceItems.add(EvidenceItems.getEvidenceItems2(evidenceItem));

        //还款记录
        List<Map<String, Object>> listRecords = arbitrationMapper.selectRepayRecords(Integer.toString(borrId));
        evidenceItems.add(EvidenceItems.getEvidenceItems3(listRecords));

        //催收记录
        List<Map<String, Object>> listRemark = arbitrationMapper.selectRemarkInfo(Integer.toString(borrId));
        evidenceItems.add(EvidenceItems.getEvidenceItems4(listRemark));

        //签章委托书
        evidenceItems.add(EvidenceItems.getEvidenceItems5(evidenceItem));

        evidences.setEvidenceItems(evidenceItems);

        List<Evidences> listEvidences = new ArrayList<>();
        listEvidences.add(evidences);
        return listEvidences;
    }

    /**
     * 处理excel中的争议金额
     */
    private String getExcelDisputeFee(ExtData extData) {
        Double liquidatedDamages = new BigDecimal(Double.parseDouble(extData.getLoanContractMoney()) * Double.parseDouble(extData.getAccountingDays()) / 365d * 0.24).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return new DecimalFormat("#.00").format(Double.parseDouble(extData.getPrinciple()) + Double.parseDouble(extData.getAccrual()) + Double.parseDouble(extData.getServiceFee1()) + Double.parseDouble(extData.getServiceFee2()) - Double.parseDouble(extData.getServiceFeeDiscount()) + liquidatedDamages);
    }

}
