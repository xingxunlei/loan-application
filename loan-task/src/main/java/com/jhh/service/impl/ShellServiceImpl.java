package com.jhh.service.impl;

import com.jhh.dao.ApplyLoanMapper;
import com.jhh.dao.CodeValueMapper;
import com.jhh.model.ApplyLoanVo;
import com.jhh.service.ShellService;
import com.loan_utils.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.Arrays;
import java.util.List;

/**
 * 2017/10/18.
 */
@Service
public class ShellServiceImpl implements ShellService {

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private CodeValueMapper codeValueMapper;

    @Autowired
    private ApplyLoanMapper applyLoanMapper;

    /**
     *  申请-审核请求地址
     */
    private static String applyLoanUrl = PropertiesReaderUtil.read("shell", "ApplyLoanUrl");
    /**
     *  贝壳钱包key
     */
    private static String Key = PropertiesReaderUtil.read("shell", "Key");

    @Override
    public void applyFail() throws Exception{

        //在redis中取出贝壳渠道编号
        String source = jedisCluster.get(ShellRedisConstant.SHELL_SOURCE);
        if (StringUtils.isEmpty(source)){
            List<String> list = codeValueMapper.getSourceByDesc(ShellConstant.DESCRIPTION);
            if (list != null && list.size()>0){
                source = StringUtils.join(list.toArray(), ",");
                jedisCluster.set(ShellRedisConstant.SHELL_SOURCE,source);
            }
        }
        //以便以后不对接贝壳
        if (source == null){
            return;
        }
        //获取审核失败合同信息
        List<ApplyLoanVo> vo = applyLoanMapper.getBorrowListByPerId(Arrays.asList(source.split(",")));
        if (vo != null && vo.size()>0){
            vo.forEach(al -> {
                al.setKey(Key);
                al.setVerify_status("1"); //被拒
                   IOUtil.postNetReturnUrl(applyLoanUrl,MapUtils.setConditionMap(al));
           });

        }


    }

}
