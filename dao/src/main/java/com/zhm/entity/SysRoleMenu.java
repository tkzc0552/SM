package com.zhm.entity;

import com.zhm.ListenerEntity.ListenerEntity;

import javax.persistence.*;

/**
 * Created by 赵红明 on 2019/8/2.
 */
@Entity
@Table(name = "sys_role_menu", schema = "systemmodel")
public class SysRoleMenu extends ListenerEntity {
    private Integer id;
    private Integer roleId;
    private Integer menuId;
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
    @Column(name = "role_id", nullable = true)
    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Basic
    @Column(name = "menu_id", nullable = true)
    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }


    @Basic
    @Column(name = "create_user", nullable = true, length = 30)
    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }



    @Basic
    @Column(name = "update_user", nullable = true, length = 30)
    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}
