package com.zhixindu.apply.core.apply.service;

import com.zhixindu.apply.facade.apply.bo.ApplyBaseInfoBO;
import com.zhixindu.apply.facade.apply.bo.ApplyCreditBO;
import com.zhixindu.apply.facade.apply.bo.ApplyStatusBO;

/**
 * Created by SteveGuo on 2017/3/7.
 */
public interface ApplyService {

    int saveApplyLoan(ApplyBaseInfoBO applyBaseInfoBO);

    String getLatestApplyStatus(Integer applyId);

    int updateApplyStatus(ApplyStatusBO applyStatusBO);

    int updateApplyCredit(ApplyCreditBO applyCreditBO);

}
