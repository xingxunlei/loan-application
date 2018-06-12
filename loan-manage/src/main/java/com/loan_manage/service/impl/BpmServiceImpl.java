package com.loan_manage.service.impl;

import com.loan_entity.manager_vo.BpmNodeVo;
import com.loan_manage.mapper.BpmNodeMapper;
import com.loan_manage.service.BpmService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Create by Jxl on 2017/9/12
 */
@Service
public class BpmServiceImpl implements BpmService {
    @Autowired
    private BpmNodeMapper bpmNodeMapper;

    @Override
    public List<BpmNodeVo> getBpmNodesByPerId(String personId) {
        if(!StringUtils.isNumeric(personId)){
            return null;
        }
        List<BpmNodeVo> nodes = bpmNodeMapper.findNodesByPersonId(Integer.valueOf(personId));
        return nodes;
    }
}
