package com.loan_manage.mapper;

import com.loan_entity.manager.MsgTemplate;
import com.loan_entity.manager_vo.MsgTemplateVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MsgTemplateMapper extends Mapper<MsgTemplate> {

    List<MsgTemplateVo> getAllMsgTemplateList();
}