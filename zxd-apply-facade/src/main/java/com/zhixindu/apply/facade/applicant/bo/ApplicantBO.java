package com.zhixindu.apply.facade.applicant.bo;

import com.zhixindu.apply.facade.applicant.enums.ApplyResult;
import com.zhixindu.apply.facade.applicant.enums.BankCardVerify;
import com.zhixindu.apply.facade.applicant.enums.LoanFillStep;
import com.zhixindu.apply.facade.applicant.enums.MobileVerify;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

public class ApplicantBO extends ApplicantBaseInfoBO implements IApplicantVerify,IApplyResult,Serializable {

    private static final long serialVersionUID = -1373173161346766532L;

    /** 手机服务密码 */
    private String service_password;
    /** 运营商token */
    private String operator_token;
    /** 手机号验证 */
    private MobileVerify mobile_verify;
    /** 银行卡验证 */
    private BankCardVerify bank_card_verify;
    /** 拒绝时间 */
    private Date reject_time;
    /** 填写步骤 */
    private LoanFillStep loan_fill_step;
    /** 申请结果 */
    private ApplyResult apply_result;
    /** 信用评分 */
    private Integer credit_score;

    public String getService_password() {
        return service_password;
    }

    public void setService_password(String service_password) {
        this.service_password = service_password == null ? null : service_password.trim();
    }

    public String getOperator_token() {
        return operator_token;
    }

    public void setOperator_token(String operator_token) {
        this.operator_token = operator_token;
    }

    public MobileVerify getMobile_verify() {
        return mobile_verify;
    }

    public void setMobile_verify(MobileVerify mobile_verify) {
        this.mobile_verify = mobile_verify;
    }

    public BankCardVerify getBank_card_verify() {
        return bank_card_verify;
    }

    public void setBank_card_verify(BankCardVerify bank_card_verify) {
        this.bank_card_verify = bank_card_verify;
    }

    public ApplyResult getApply_result() {
        return apply_result;
    }

    public void setApply_result(ApplyResult apply_result) {
        this.apply_result = apply_result;
    }

    public Date getReject_time() {
        return reject_time;
    }

    public void setReject_time(Date reject_time) {
        this.reject_time = reject_time;
    }

    public LoanFillStep getLoan_fill_step() {
        return loan_fill_step;
    }

    public void setLoan_fill_step(LoanFillStep loan_fill_step) {
        this.loan_fill_step = loan_fill_step;
    }

    public Integer getCredit_score() {
        return credit_score;
    }

    public void setCredit_score(Integer credit_score) {
        this.credit_score = credit_score;
    }

    @Override
    public boolean isLowCredit() {
        return  null != getApply_result() && null != getReject_time()
                && ApplyResult.REJECT.matches(getApply_result())
                && DateTime.now().minusMonths(1).isBefore(getReject_time().getTime());
    }

    @Override
    public boolean hasNotVerifiedItem() {
        return (null != getMobile_verify() && !isMobileVerified())
                || (null != getBank_card_verify() && !isBankCardVerified());
    }

    @Override
    public boolean isMobileVerified() {
        return MobileVerify.VERIFIED.matches(getMobile_verify());
    }

    @Override
    public boolean isBankCardVerified() {
        return BankCardVerify.VERIFIED.matches(getBank_card_verify());
    }
}