package com.loan_manage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.loan_api.contract.ElectronicContractService;
import com.loan_entity.contract.Contract;
import com.loan_manage.exception.ArbitrationException;
import com.loan_manage.mapper.ArbitrationMapper;
import com.loan_manage.service.AgreementService;
import com.loan_manage.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xingmin
 */
@Slf4j
@Service
public class AgreementServiceImpl implements AgreementService {

    @Autowired
    private ElectronicContractService electronicContractService;

    @Autowired
    private ArbitrationMapper arbitrationMapper;

    private static final int CONTRACT_STATUS_1 = 1;
    private static final int CONTRACT_STATUS_2 = 2;
    private static final int CONTRACT_STATUS_3 = 3;
    private static final String JSON_RETURN_CODE_10000 = "10000";
    private static final String JSON_RETURN_CODE_KEY = "code";
    private static final String JSON_RETURN_DATA_KEY = "data";
    private static final String ORDER_SAVE_PATH = "D:/contracts/orderMap.txt";
    private static final String FILE_SAVE_PATH = "D:/contracts/%s.pdf";
    private static final String CONTRACT_PRODUCT_ID = "19";

    @Override
    public List<Map<String, String>> generateFiles(String perId, String borrId) {
        List<Map<String, String>> list = getBorrList(perId, borrId);

        List<Map<String, String>> results = new ArrayList<>();
        for (Map<String, String> map : list) {
            String fileName;
            try {
                fileName = generateMaterials(map.get("perId"), map.get("borrId"));
                map.put("message", fileName);
                map.put("code", "SUCCESS");
            } catch (ArbitrationException e) {
                map.put("message", e.getMessage());
                map.put("code", "FAIL");
            }

            results.add(map);
        }
        return results;
    }

    /**
     * 数据列表
     */
    private List<Map<String, String>> getBorrList(String perId, String borrId) {
        List<Map<String, String>> list = new ArrayList<>();
        if (!StringUtils.isEmpty(perId) && !StringUtils.isEmpty(borrId)) {
            Map<String, String> map = new HashMap<>(16);
            map.put("perId", perId);
            map.put("borrId", borrId);
            list.add(map);

            return list;
        }

        List<String> datas = FileUtils.readFileByLine(ORDER_SAVE_PATH);
        datas.forEach(s -> {
            Map<String, String> map = new HashMap<>(16);
            map.put("borrId", s.split(",")[0]);
            map.put("perId", s.split(",")[1]);
            list.add(map);
        });

        return list;
    }

    /**
     * 生成材料
     */
    private String generateMaterials(String perId, String borrId) {
        try {
            // 生成借款协议
            generateBorrowAgreement(borrId);

            // 下载借款协议
            return getBorrowAgreement(borrId, perId);
        }catch (Exception e) {
            throw new ArbitrationException(e);
        }
    }

    /**
     * 仲裁数据-电子合同（借款协议-电子签章）
     */
    private void generateBorrowAgreement(String borrId) {
        String contract = electronicContractService.createElectronicContract(Integer.parseInt(borrId), CONTRACT_PRODUCT_ID);
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
     * 准备材料-借款协议
     */
    private String getBorrowAgreement(String borrId, String perId) {
        Contract contract = new Contract();
        contract.setProductId(CONTRACT_PRODUCT_ID);
        contract.setBorrId(Integer.parseInt(borrId));
        contract = arbitrationMapper.selectContract(contract);
        if (contract == null || contract.getStatus() == CONTRACT_STATUS_1) {
            log.info("getBorrowAgreement【"+ borrId +","+ perId +"】-------准备材料-借款协议尚未生成 1s 后重新获取---------");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getBorrowAgreement(borrId, perId);
        }

        if (contract.getStatus() == CONTRACT_STATUS_2) {
            log.info("getBorrowAgreement【"+ borrId +","+ perId +"】-------准备材料-借款协议生成中 1s 后重新获取---------");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getBorrowAgreement(borrId, perId);
        }

        if (contract.getStatus() != CONTRACT_STATUS_3) {
            log.info("getBorrowAgreement【"+ borrId +","+ perId +"】-------准备材料-借款协议生成失败---------");
            throw new ArbitrationException("getBorrowAgreement【"+ borrId +","+ perId +"】-------准备材料-借款协议生成失败---------");
        }

        Map<String, String> map = arbitrationMapper.queryCardInfoById(perId);

        try {
            FileUtils.downloadFile(contract.getContractUrl(), String.format(FILE_SAVE_PATH, map.get("perName")));
        } catch (Exception e) {
            log.info("getBorrowAgreement【"+ borrId +","+ perId +"】-------准备材料-下载借款协议出现错误---------");
            throw new ArbitrationException(e.getMessage(), e.getCause());
        }
        return String.format(FILE_SAVE_PATH, map.get("perName"));
    }
}
