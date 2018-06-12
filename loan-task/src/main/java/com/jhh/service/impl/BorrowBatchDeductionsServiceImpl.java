package com.jhh.service.impl;

import com.jhh.dao.BorrowDeductionsMapper;
import com.jhh.model.BorrowDeductions;
import com.jhh.service.BorrowBatchDeductionsService;
import com.jhh.util.Detect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service("borrowBatchDeductionsService")
public class BorrowBatchDeductionsServiceImpl implements BorrowBatchDeductionsService {

    private static final Logger logger = LoggerFactory.getLogger(BorrowBatchDeductionsService.class);

    @Autowired
    BorrowDeductionsMapper borrowDeductionsMapper;

    @Override
    public void creatFullDeductionsStatus() {
        List<BorrowDeductions> list = borrowDeductionsMapper.selectAllData();
        logger.error("creatFullDeductionsStatus" + list.size());
        if(Detect.notEmpty(list)) {
            List<BorrowDeductions> bds = new ArrayList();
            int count = 0;
            for (BorrowDeductions bd : list) {
                count++;
                bds.add(bd);
                if (bds.size() == 1000 || count == list.size()) {
                    logger.error("count:" + count);
                    borrowDeductionsMapper.createBorrowDeductions(bds);
                    bds = new ArrayList();
                }
            }
        }
    }

    @Override
    public int saveAgriculturalBankStatus() {
        List ids = borrowDeductionsMapper.selectIdByBankId();
        logger.error("saveAgriculturalBankStatus count:" + ids.size());
        return borrowDeductionsMapper.saveFailSatuts(ids);
    }

    @Override
    public int saveDeductionsStatus() {
        List<BorrowDeductions> list = borrowDeductionsMapper.selectByYesterdayOrder();
        if(Detect.notEmpty(list)){
            return borrowDeductionsMapper.saveByYesterdayOrder(list);
        }
        return 0;
    }


}