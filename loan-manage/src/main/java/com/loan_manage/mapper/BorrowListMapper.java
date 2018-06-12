package com.loan_manage.mapper;

import com.loan_entity.app.BorrowList;
import com.loan_entity.manager.CollectorsListVo;
import com.loan_entity.utils.RepaymentDetails;
import com.loan_manage.entity.arbitration.TemplateExcl;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BorrowListMapper extends Mapper<BorrowList> {

    List<BorrowList> getBorrList();

    List<BorrowList> selectUnBaikelu();

    int rejectAudit();

    List<BorrowList> queryBorrListByPerIdAndStauts(Integer perId);

    Map selectNow(@Param("per_id") Integer per_id);
}
