/*
 * Copyright (C) 2016 YuWei. All rights reserved.
 * You can get our information at http://www.zhixindu.com
 * Anyone can't use this file without our permission.
 */
package com.zhixindu.apply.core.test;

import com.zhixindu.apply.core.app.DatabaseConfig;
import com.zhixindu.apply.core.app.WebAppConfig;
import com.zhixindu.apply.core.lender.dao.LenderAddressMapper;
import com.zhixindu.apply.facade.apply.bo.ApplyMgtDetailBO;
import com.zhixindu.apply.facade.workflow.enums.StepDefinition;
import com.zhixindu.commons.bean.IEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yulei
 * @version 1.0
 * @date 03/06/2017
 * @description
 */
public class MgtBusinessImplTest {
    /*private final static Logger LOGGER = LoggerFactory.getLogger(MgtBusinessImplTest.class);

    @Inject
    private LenderAddressMapper lenderAddressMapper;

    @Test
    public void getLenderTest(){
        System.out.println(lenderAddressMapper.selectByLenderId(1));
        System.out.println("fdsasfd");
    }
*/
    public static void main(String [] args){
        ApplyMgtDetailBO bo = new ApplyMgtDetailBO();
        bo.setStep_definition_id(3);
        bo.setProcessing_state(0);
        System.out.println(bo.getApply_status_desc());

//        System.out.println(StepDefinition1.resolve(1).getDesc());
    }
}
