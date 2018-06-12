package com.loan_manage.mapper;

import com.loan_entity.app.BpmNode;
import com.loan_entity.manager_vo.BpmNodeVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


/**
 * Create by Jxl on 2017/9/12
 */
public interface BpmNodeMapper extends Mapper<BpmNode> {
    List<BpmNodeVo> findNodesByPersonId(Integer personId);
}
