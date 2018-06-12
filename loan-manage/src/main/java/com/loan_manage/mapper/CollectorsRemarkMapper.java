package com.loan_manage.mapper;

import com.loan_entity.manager.CollectorsRemark;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface CollectorsRemarkMapper extends Mapper<CollectorsRemark> {

    public List<CollectorsRemark> selectRemarkInfo(Map<String, Object> paramMap);
}
