package com.zhixindu.apply.core.test;

import com.zhixindu.apply.core.app.WebAppConfig;
import com.zhixindu.apply.core.lender.dao.LenderMapper;
import com.zhixindu.apply.core.lender.po.LenderBaseInfoPO;
import com.zhixindu.apply.core.lender.service.LenderService;
import com.zhixindu.apply.facade.lender.bo.MobileVerifyBO;
import com.zhixindu.apply.facade.lender.enums.FillStep;
import com.zhixindu.apply.facade.lender.enums.MobileVerify;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

/**
 * Created by SteveGuo on 2017/3/8.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebAppConfig.class})
@WebAppConfiguration
public class LenderWechatBusinessTest {

    @Inject
    private LenderMapper lenderMapper;
    @Inject
    private LenderService lenderService;

    @Test
    public void testUpdateMobileVerify(){
        MobileVerifyBO mobileVerifyBO = new MobileVerifyBO();
        mobileVerifyBO.setLender_id(1);
        mobileVerifyBO.setMobile("17091918167");
        mobileVerifyBO.setMobile_verify(MobileVerify.VERIFIED);
        System.out.println(lenderMapper.updateMobileVerify(mobileVerifyBO));
    }

    @Test
    public void testSelectByCustomerId(){
        System.out.println(lenderMapper.selectByCustomerId("123"));
    }

    @Test
    public void testInserBaseInfo(){
        LenderBaseInfoPO lenderBaseInfoPO = new LenderBaseInfoPO();
        lenderBaseInfoPO.setCustomer_id("1298765");
        lenderBaseInfoPO.setMobile("18766223455");
        lenderBaseInfoPO.setId_card("3565778765431");
        lenderBaseInfoPO.setName("abc2");
        lenderBaseInfoPO.setFill_step(FillStep.BASIC_INFO);
        System.out.println(lenderService.saveLenderBaseInfo(lenderBaseInfoPO));
        System.out.println(lenderBaseInfoPO.getLender_id());
    }


}
