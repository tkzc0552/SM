package com.zhm.entity;

import com.zhm.ListenerEntity.ListenerEntity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by 赵红明 on 2019/8/3.
 */
@Entity
@Table(name = "sys_user_role", schema = "systemmodel")
public class SysUserRole extends ListenerEntity {
    private Integer id;
    private Integer userId;
    private Integer roleId;
    private String createUser;
    private String updateUser;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "user_id", nullable = true)
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "role_id", nullable = true)
    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }



    @Basic
    @Column(name = "create_user", nullable = true, length = 40)
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
