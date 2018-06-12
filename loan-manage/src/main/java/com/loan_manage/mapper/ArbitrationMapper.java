package com.loan_manage.mapper;

import com.loan_entity.contract.Contract;
import com.loan_manage.entity.arbitration.EvidenceItemsExtData;
import com.loan_manage.entity.arbitration.ExtData;
import com.loan_manage.entity.arbitration.TemplateExcl;

import java.util.List;
import java.util.Map;

public interface ArbitrationMapper {

    Contract selectContract(Contract contract);

    List<Map<String, Object>> selectRemarkInfo(String borrId);

    Map<String,String> queryCardInfoById(String perId);

    List<Map<String,Object>> selectRepayRecords(String borrId);

    TemplateExcl selectTemplateExclOneLine(Integer borrId);

    TemplateExcl selectTemplateExclThreeLine(Integer borrId);

    ExtData selectExtDataByBorrId(Integer borrId);

    EvidenceItemsExtData selectEvidenceItemsExtDataByBorrId(Integer borrId);
}
