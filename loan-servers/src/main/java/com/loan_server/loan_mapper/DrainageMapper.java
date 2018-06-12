package com.loan_server.loan_mapper;

import com.loan_entity.drainage.Drainage;
import com.loan_entity.drainage.DrainageStat;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by zhangqi on 17-11-20.
 */
public interface DrainageMapper {

    /**
     * TODO
     * @return
     */
    List<Drainage> selectDrainage(@Param("status") String status);

    /**
     * 批量插入引流日志数据
     * @param drainageStatList
     * @return
     */
    int insertDrainageStat(List<DrainageStat> drainageStatList);

}
