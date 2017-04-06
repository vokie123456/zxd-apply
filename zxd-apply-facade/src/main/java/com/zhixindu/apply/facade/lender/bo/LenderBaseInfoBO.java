package com.zhixindu.apply.facade.lender.bo;

import com.zhixindu.commons.api.utils.MaskUtil;

import java.io.Serializable;

public class LenderBaseInfoBO implements Serializable {

    private static final long serialVersionUID = 1046135059755108694L;

    /** 借款人ID */
    private Integer lender_id;
    /** 客户ID */
    private String customer_id;
    /** 手机号 */
    private String mobile;
    /** 姓名 */
    private String name;
    /** 身份证 */
    private String id_card;

    public Integer getLender_id() {
        return lender_id;
    }

    public void setLender_id(Integer lender_id) {
        this.lender_id = lender_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id == null ? null : customer_id.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card == null ? null : id_card.trim();
    }

    public String getMaskMobile(){
        return MaskUtil.maskMobile(getMobile());
    }

    public String getMaskIdCard(){
        return MaskUtil.maskIdNo(getId_card());
    }

}