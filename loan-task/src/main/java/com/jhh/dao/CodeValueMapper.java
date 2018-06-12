package com.jhh.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *  字典表
 */
public interface CodeValueMapper {

    /**
     *  根据 description 查询数据来源
     * @param description 描述
     * @return
     */
    public List<String> getSourceByDesc(@Param("description") String description);
}
