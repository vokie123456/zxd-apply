package com.zhixindu.apply.core.applicant.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.zhixindu.apply.core.apply.dao.ApplyAddressMapper;
import com.zhixindu.apply.core.apply.dao.ApplyBankCardMapper;
import com.zhixindu.apply.core.apply.dao.ApplyContactMapper;
import com.zhixindu.apply.core.applicant.dao.ApplicantMapper;
import com.zhixindu.apply.core.system.service.SystemConfigService;
import com.zhixindu.apply.facade.apply.bo.ApplyAddressBO;
import com.zhixindu.apply.facade.applicant.bo.ApplyAddressMgtBO;
import com.zhixindu.apply.facade.applicant.bo.ApplicantBO;
import com.zhixindu.apply.facade.apply.bo.ApplyBankCardBO;
import com.zhixindu.apply.facade.apply.bo.ApplyContactBO;
import com.zhixindu.apply.facade.applicant.bo.ApplicantInfoBO;
import com.zhixindu.apply.facade.applicant.bo.ApplicantMgtInfo;
import com.zhixindu.apply.facade.applicant.bo.ApplicantMgtQueryParam;
import com.zhixindu.apply.facade.applicant.business.DubboApplyApplicantMgtBusiness;
import com.zhixindu.commons.annotation.Business;
import com.zhixindu.commons.api.ServiceCode;
import com.zhixindu.commons.api.ServiceException;
import com.zhixindu.commons.page.PageResult;
import com.zhixindu.commons.utils.Parameters;
import org.springframework.beans.BeanUtils;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by SteveGuo on 2017/3/3.
 */
@Business("lenderMgtBusiness")
public class ApplicantMgtBusinessImpl implements DubboApplyApplicantMgtBusiness {

    @Inject
    private ApplicantMapper applicantMapper;
    @Inject
    private ApplyContactMapper applyContactMapper;
    @Inject
    private ApplyBankCardMapper applyBankCardMapper;
    @Inject
    private ApplyAddressMapper applyAddressMapper;
    @Inject
    private SystemConfigService systemConfigService;

    @Override
    public PageResult<ApplicantInfoBO> findApplicantInfoByPage(ApplicantMgtQueryParam param) throws ServiceException {
        return null;
    }

    @Override
    public ApplicantMgtInfo findApplicantInfo(Integer applicant_id) throws ServiceException {
        Parameters.requireNotNull(applicant_id,"findLenderInfo applicant_id illegal_param !");
        ApplicantBO lender = applicantMapper.selectByPrimaryKey(applicant_id);
        if(null == lender){
            throw new ServiceException(ServiceCode.NO_RESULT,"查询的申请信息无结果!!!");
        }
        ApplicantMgtInfo applicantMgtInfo = new ApplicantMgtInfo();
        BeanUtils.copyProperties(lender, applicantMgtInfo);
        ApplyBankCardBO applyBankCardBO = applyBankCardMapper.selectByApplicantId(lender.getApplicant_id());
        if(applyBankCardBO != null){
            applicantMgtInfo.setApplyBankCardBO(applyBankCardBO);
        }

        List<ApplyContactBO> applyContactBOList = applyContactMapper.selectByApplicantId(lender.getApplicant_id());
        if(CollectionUtils.isNotEmpty(applyContactBOList)){
            applicantMgtInfo.setApplyContactBOS(applyContactBOList);
        }
        ApplyAddressBO applyAddressBO = applyAddressMapper.selectByApplicantId(lender.getApplicant_id());
        if(applyAddressBO != null){
            ApplyAddressMgtBO lenderAddressMgtBO = new ApplyAddressMgtBO();
            BeanUtils.copyProperties(applyAddressBO,lenderAddressMgtBO);
            if(null != applyAddressBO.getHome_address_code()){
                String homeAddressInfo = systemConfigService.getRegionFullName(applyAddressBO.getHome_address_code());
                lenderAddressMgtBO.setHome_address_info(homeAddressInfo);
            }
            if(null != applyAddressBO.getCompany_address_code()) {
                String companyAddressInfo = systemConfigService.getRegionFullName(applyAddressBO.getCompany_address_code());
                lenderAddressMgtBO.setCompany_address_info(companyAddressInfo);
            }
            applicantMgtInfo.setLenderAddressMgtBO(lenderAddressMgtBO);
        }
        return applicantMgtInfo;
    }
}
