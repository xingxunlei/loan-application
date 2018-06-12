package com.loan_manage.service.impl;

import com.loan_manage.mapper.DrainageMapper;
import com.loan_manage.service.BaseDrainageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DrainageServiceImpl implements BaseDrainageService<Map,Map> {
    //引流图片保存路径
    @Value("${drainageImageDir}")
    private String drainageImageDir;

    @Autowired
    private DrainageMapper drainageMapper;
    @Override
    public int addDrainage(Map map) {
        return drainageMapper.insertDrainage(map);
    }

    @Override
    public int deleteDrainage(String ids) {
        return drainageMapper.deleteDrainageById(ids);
    }

    @Override
    public int updateDrainage(Map map) {
        return drainageMapper.updateDrainage(map);
    }

    @Override
    public List<Map> queryDrains(Map map) {
        return drainageMapper.queryDrainages(map);
    }

    @Override
    public Map queryById(String id) {
        return drainageMapper.queryById(id);
    }

    public String getDrainageImageDir() {
        return drainageImageDir;
    }

    public void setDrainageImageDir(String drainageImageDir) {
        this.drainageImageDir = drainageImageDir;
    }
}
