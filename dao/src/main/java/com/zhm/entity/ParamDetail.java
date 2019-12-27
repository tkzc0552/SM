package com.zhm.entity;

import com.zhm.ListenerEntity.ListenerEntity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by 赵红明 on 2019/7/31.
 */
@Entity
@Table(name = "param_detail", schema = "systemmodel")
public class ParamDetail extends ListenerEntity {
    private Integer paramDetailId;
    private String paramCode;
    private String paramDetailCode;
    private String paramDetailCname;
    private String paramDetailEname;
    private String paramDetailNote;
    private String warehouse;
    private Integer sortNum;
    private String createUser;
    private String updateUser;

    @Id
    @Column(name = "param_detail_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getParamDetailId() {
        return paramDetailId;
    }

    public void setParamDetailId(Integer paramDetailId) {
        this.paramDetailId = paramDetailId;
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
    @Column(name = "param_detail_code", nullable = false, length = 100)
    public String getParamDetailCode() {
        return paramDetailCode;
    }

    public void setParamDetailCode(String paramDetailCode) {
        this.paramDetailCode = paramDetailCode;
    }

    @Basic
    @Column(name = "param_detail_cname", nullable = true, length = 100)
    public String getParamDetailCname() {
        return paramDetailCname;
    }

    public void setParamDetailCname(String paramDetailCname) {
        this.paramDetailCname = paramDetailCname;
    }

    @Basic
    @Column(name = "param_detail_ename", nullable = true, length = 255)
    public String getParamDetailEname() {
        return paramDetailEname;
    }

    public void setParamDetailEname(String paramDetailEname) {
        this.paramDetailEname = paramDetailEname;
    }

    @Basic
    @Column(name = "param_detail_note", nullable = true, length = 100)
    public String getParamDetailNote() {
        return paramDetailNote;
    }

    public void setParamDetailNote(String paramDetailNote) {
        this.paramDetailNote = paramDetailNote;
    }

    @Basic
    @Column(name = "warehouse", nullable = true, length = 20)
    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    @Basic
    @Column(name = "sort_num", nullable = true)
    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
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
