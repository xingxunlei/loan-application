package com.loan_manage.mapper;

import com.loan_entity.manager.Msg;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MsgMapper extends Mapper<Msg> {

    List<Msg> getMessageByUserId(String id, int start, int pageSize);
    
    int updateMessageStatus(String msgId);
    
    int selectUnread(String perId);
}