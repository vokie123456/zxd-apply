package com.zhixindu.apply.core.apply.business;

import com.google.common.collect.Lists;
import com.zhixindu.apply.core.apply.dao.ApplyLocationMapper;
import com.zhixindu.apply.core.apply.dao.ApplyMapper;
import com.zhixindu.apply.core.apply.dao.ApplyStepMapper;
import com.zhixindu.apply.core.apply.po.ApplyPO;
import com.zhixindu.apply.core.apply.service.ApplyService;
import com.zhixindu.apply.core.constant.ApplyErrorCode;
import com.zhixindu.apply.core.lender.service.LenderService;
import com.zhixindu.apply.facade.apply.bo.ApplyBO;
import com.zhixindu.apply.facade.apply.bo.ApplyBaseInfoBO;
import com.zhixindu.apply.facade.apply.bo.ApplyCreditBO;
import com.zhixindu.apply.facade.apply.bo.ApplyLoanBO;
import com.zhixindu.apply.facade.apply.bo.ApplyLoanDetailBO;
import com.zhixindu.apply.facade.apply.bo.ApplyLoanStepBO;
import com.zhixindu.apply.facade.apply.bo.ApplyLocationBO;
import com.zhixindu.apply.facade.apply.bo.ApplyPageParam;
import com.zhixindu.apply.facade.apply.bo.ApplyStatusBO;
import com.zhixindu.apply.facade.apply.bo.ApplyStepBO;
import com.zhixindu.apply.facade.apply.business.DubboApplyWechatBusiness;
import com.zhixindu.apply.facade.apply.enums.ApplyStatus;
import com.zhixindu.apply.facade.apply.enums.ProcessState;
import com.zhixindu.apply.facade.apply.enums.ProcessStep;
import com.zhixindu.commons.annotation.Business;
import com.zhixindu.commons.api.ServiceCode;
import com.zhixindu.commons.api.ServiceException;
import com.zhixindu.commons.page.PageResult;
import com.zhixindu.commons.repository.PageRepository;
import com.zhixindu.commons.utils.Parameters;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by SteveGuo on 2017/3/3.
 */
@Business("applyWechatBusiness")
public class ApplyWechatBusinessImpl implements DubboApplyWechatBusiness {

    @Inject
    private ApplyService applyService;
    @Inject
    private LenderService lenderService;
    @Inject
    private ApplyMapper applyMapper;
    @Inject
    private ApplyStepMapper applyStepMapper;
    @Inject
    private ApplyLocationMapper applyLocationMapper;
    @Inject
    private PageRepository pageRepository;

    @Override
    public boolean isBeforeAMonthFromLastApply(Integer lenderId) {
        Parameters.requireNotNull(lenderId, "lenderId不能为空");
        Date lastApplyTime = applyMapper.selectLastApplyTime(lenderId);
        return null != lastApplyTime && DateTime.now().minusMonths(1).isBefore(lastApplyTime.getTime());
    }

    @Override
    public boolean hasNotSettledApply(Integer lenderId) {
        Parameters.requireNotNull(lenderId, "lenderId不能为空");
        return applyMapper.countNotSettledApply(lenderId) > 0;
    }

    @Override
    public boolean hasSettledApply(Integer lenderId) throws ServiceException {
        ApplyPO applyPO = applyMapper.selectLatestByLenderId(lenderId);
        return null != applyPO && ApplyStatus.REPAYMENT_SETTLED.matches(applyPO.getApply_status());
    }

    @Override
    public ApplyBaseInfoBO findLatestReviewApply(Integer lenderId) {
        Parameters.requireNotNull(lenderId, "lenderId不能为空");
        return applyMapper.selectLatestReviewByLenderId(lenderId);
    }

    @Override
    public Integer submitApplyLoan(ApplyBaseInfoBO applyBaseInfoBO) {
        Parameters.requireAllPropertyNotNull(applyBaseInfoBO, new Object[]{"apply_id"});
        Integer lenderId = applyBaseInfoBO.getLender_id();
        if(!lenderService.hasMobileVerified(lenderId)) {
            throw new ServiceException(ApplyErrorCode.MOBILE_NOT_VERIFIED.getErrorCode(), ApplyErrorCode
                    .MOBILE_NOT_VERIFIED.getDesc());
        }
        if(!lenderService.hasBankCardVerified(lenderId)) {
            throw new ServiceException(ApplyErrorCode.BANK_CARD_NOT_VERIFIED.getErrorCode(), ApplyErrorCode
                    .BANK_CARD_NOT_VERIFIED.getDesc());
        }
        if(hasNotSettledApply(lenderId)) {
            throw new ServiceException(ApplyErrorCode.HAS_NOT_SETTLED_APPLY.getErrorCode(), ApplyErrorCode
                    .HAS_NOT_SETTLED_APPLY.getDesc());
        }
        return applyService.saveApplyLoan(applyBaseInfoBO);
    }

    @Override
    public PageResult<ApplyLoanBO> findApplyLoanList(ApplyPageParam pageParam) {
        Parameters.requireAllPropertyNotNull(pageParam, "分页查询参数不能为空");
        PageResult<ApplyPO> applyBOPageResult = pageRepository.selectPaging(ApplyMapper.class, "selectListByPage", pageParam);
        PageResult<ApplyLoanBO> pageResult = new PageResult<>();
        BeanUtils.copyProperties(applyBOPageResult, pageResult);

        List<ApplyPO> applyPOList = applyBOPageResult.getRows();
        List<ApplyLoanBO> applyLoanBOList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(applyPOList)){
            applyLoanBOList = applyPOList.stream().map(applyBO -> {
                ApplyLoanBO applyLoanBO = new ApplyLoanBO();
                BeanUtils.copyProperties(applyBO, applyLoanBO);
                applyLoanBO.setApply_time(new DateTime(applyBO.getApply_time()).toString("yyyy-MM-dd"));
                applyLoanBO.setApply_status(applyBO.getApply_status().getDesc());
                applyLoanBO.setApply_status_value(applyBO.getApply_status().getValue());
                return applyLoanBO;
            }).collect(Collectors.toList());
        }
        pageResult.setRows(applyLoanBOList);
        return pageResult;
    }

    @Override
    public ApplyLoanDetailBO findApplyLoanDetail(Integer applyId) {
        Parameters.requireNotNull(applyId, "applyId不能为空");
        ApplyLoanDetailBO applyLoanDetailBO = new ApplyLoanDetailBO();
        ApplyPO applyPO = applyMapper.selectByPrimaryKey(applyId);
        if(null == applyPO) {
            throw new ServiceException(ServiceCode.NO_RESULT, "没有匹配的申请借款记录");
        }
        BeanUtils.copyProperties(applyPO, applyLoanDetailBO);
        applyLoanDetailBO.setApply_time(new DateTime(applyPO.getApply_time()).toString("yyyy-MM-dd HH:mm:ss"));
        applyLoanDetailBO.setApply_status(applyPO.getApply_status().getDesc());
        applyLoanDetailBO.setApply_status_value(applyPO.getApply_status().getValue());

        List<ApplyStepBO> applyStepBOList = applyStepMapper.selectListByApplyId(applyId);
        List<ApplyLoanStepBO> applyLoanStepBOList = Lists.newArrayListWithCapacity(0);
        if(CollectionUtils.isNotEmpty(applyStepBOList)) {
            applyLoanStepBOList = applyStepBOList.stream().map(instanceBO -> {
                ApplyLoanStepBO applyWorkflowBO = new ApplyLoanStepBO();
                ProcessStep processStep = instanceBO.getProcess_step();
                ProcessState processState = instanceBO.getProcess_state();
                ApplyStatus applyStatus = ApplyStatus.resolve(processStep, processState);
                applyWorkflowBO.setProcess_result(applyStatus.getDesc());
                applyWorkflowBO.setProcess_result_value(applyStatus.getValue());
                if(ProcessState.PROCESSING.matches(processState)) {
                    applyWorkflowBO.setProcess_time(processStep.getProcessDesc());
                } else {
                    applyWorkflowBO.setProcess_time(new DateTime(instanceBO.getProcess_time()).toString("yyyy-MM-dd HH:mm:ss"));
                }
                return applyWorkflowBO;
            }).collect(Collectors.toList());
        }
        applyLoanDetailBO.setApplyLoanStepBOList(applyLoanStepBOList);
        return applyLoanDetailBO;
    }

    @Override
    public ApplyBO findApply(Integer applyId) {
        Parameters.requireNotNull(applyId, "applyId不能为空");
        ApplyPO applyPO = applyMapper.selectByPrimaryKey(applyId);
        if(null != applyPO) {
            ApplyLocationBO applyLocationBO = applyLocationMapper.selectByApplyId(applyPO.getApply_id());
            applyPO.setLatitude(applyLocationBO.getLatitude());
            applyPO.setLongitude(applyLocationBO.getLongitude());
            applyPO.setPrecision(applyLocationBO.getPrecision());
        }
        ApplyBO applyBO = new ApplyBO();
        BeanUtils.copyProperties(applyPO, applyBO);
        return applyBO;
    }

    @Override
    public boolean submitApplyCredit(ApplyCreditBO applyCreditBO) {
        Object[] ignoreProperties = new Object[]{};
        if(ApplyStatus.REVIEW_FAIL.matches(applyCreditBO.getApply_status())) {
            ignoreProperties = new Object[]{"credit_score"};
        }
        Parameters.requireAllPropertyNotNull(applyCreditBO, ignoreProperties);
        return applyService.updateApplyCredit(applyCreditBO) > 0;
    }

    @Override
    public boolean submitLoanStatus(ApplyStatusBO applyStatusBO) {
        Parameters.requireAllPropertyNotNull(applyStatusBO);
        return applyService.updateLoanStatus(applyStatusBO) > 0;
    }

    @Override
    public boolean submitRepaymentStatus(ApplyStatusBO applyStatusBO) {
        Parameters.requireAllPropertyNotNull(applyStatusBO, new Object[]{"apply_status"});
        return applyService.updateRepaymentStatus(applyStatusBO) > 0;
    }
}