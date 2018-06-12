package com.loan_server.app_mapper;

import com.loan_entity.app.RequestMessage;

public interface RequestMessageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RequestMessage record);

    int insertSelective(RequestMessage record);

    RequestMessage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RequestMessage record);

    int updateByPrimaryKey(RequestMessage record);
}