package com.zhm.entity;

import com.zhm.ListenerEntity.ListenerEntity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by 赵红明 on 2019/7/31.
 */
@Entity
public class Params extends ListenerEntity {
    private Integer paramId;
    private String paramCode;
    private String paramValueEn;
    private String paramValue;
    private String paramOrder;
    private String paramNote;
    private String paramType;
    private String createUser;
    private String updateUser;

    @Id
    @Column(name = "param_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getParamId() {
        return paramId;
    }

    public void setParamId(Integer paramId) {
        this.paramId = paramId;
    }

    @Basic
    @Column(name = "param_code", nullable = false, length = 40)
    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    @Basic
    @Column(name = "param_value_en", nullable = true, length = 200)
    public String getParamValueEn() {
        return paramValueEn;
    }

    public void setParamValueEn(String paramValueEn) {
        this.paramValueEn = paramValueEn;
    }

    @Basic
    @Column(name = "param_value", nullable = false, length = 100)
    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    @Basic
    @Column(name = "param_order", nullable = true, length = 10)
    public String getParamOrder() {
        return paramOrder;
    }

    public void setParamOrder(String paramOrder) {
        this.paramOrder = paramOrder;
    }

    @Basic
    @Column(name = "param_note", nullable = true, length = 255)
    public String getParamNote() {
        return paramNote;
    }

    public void setParamNote(String paramNote) {
        this.paramNote = paramNote;
    }

    @Basic
    @Column(name = "param_type", nullable = true, length = 10)
    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }


    @Basic
    @Column(name = "create_user", nullable = false, length = 40)
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }


    @Basic
    @Column(name = "update_user", nullable = true, length = 40)
    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

}
