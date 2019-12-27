package com.zhm.dto;

import java.io.Serializable;

/**
 * 系统参数DTO
 */
public class ParamDetailDTO implements Serializable {

    private String code;

    private String cname;

    private String ename;

    private String countryName;

    private String orgCode;

    private String comment;

    private boolean disabled;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "ParamDetailDTO{" +
                "code='" + code + '\'' +
                ", cname='" + cname + '\'' +
                ", ename='" + ename + '\'' +
                ", countryName='" + countryName + '\'' +
                ", orgCode='" + orgCode + '\'' +
                '}';
    }
}