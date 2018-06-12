package com.loan_manage.mapper;

import com.loan_entity.loan.CollectorsList;
import com.loan_entity.loan.CollectorsVo;
import com.loan_entity.loan.ExportWorkReport;
import com.loan_entity.manager.CollectorsCompanyVo;
import com.loan_entity.manager.CollectorsListVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface CollectorsListMapper extends Mapper<CollectorsList> {

    /**
     * 根据合同ID获取催收人及所属公司
     * @param map
     * @return
     */
    CollectorsCompanyVo selectCollectorsCompanyVo(Map<String,Object> map);
    /**
     * 根据合同号获取催收信息
     * @param map
     * @return
     */
    CollectorsVo selectCollectorsInfo(Map<String,Object> map);
    
    
    /**
     * 获取工作报告
     * @param map
     * @return
     */
    List<ExportWorkReport> getWorkReport(Map<String,Object> map);

    /**
     * 获取所有正在转件的记录合同号
     * @return
     */
    List<Integer> selectUndoneContractNos();

    /**
     * 批量插入
     * @param list
     */
    int batchInsertCollectorsList(@Param("list") List<CollectorsList> list);

    /**
     * 批量更新
     * @param list
     */
    int batchUpdateCollectorsList(@Param("list") List<CollectorsList> list);

    /**
     * 查询在特殊名下的单子
     * @return
     */
    List<CollectorsList> selectSystemCollectorsLists(Map<String,Object> map);
}
