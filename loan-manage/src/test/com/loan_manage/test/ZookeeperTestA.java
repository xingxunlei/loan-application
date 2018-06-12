package com.loan_manage.test;

import com.loan_manage.service.MasterService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-dao.xml","classpath:spring/applicationContext-service.xml"})
public class ZookeeperTestA {

    @Autowired
    MasterService masterService;

    @Test
    public void testZookeeper(){
        Assert.assertTrue(masterService.isMaster());
    }
}
