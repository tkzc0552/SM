package com.zhm.entity;

import com.zhm.ListenerEntity.ListenerEntity;

import javax.persistence.*;

/**
 * Created by 赵红明 on 2019/7/31.
 */
@Entity
@Table(name = "random_code", schema = "systemmodel")
public class RandomCode extends ListenerEntity {
    private Integer id;
    private Integer userId;
    private String randomCode;
    private Integer pastTime;
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
    @Column(name = "user_id", nullable = false)
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "random_code", nullable = true, length = 10)
    public String getRandomCode() {
        return randomCode;
    }

    public void setRandomCode(String randomCode) {
        this.randomCode = randomCode;
    }

    @Basic
    @Column(name = "past_time", nullable = true)
    public Integer getPastTime() {
        return pastTime;
    }

    public void setPastTime(Integer pastTime) {
        this.pastTime = pastTime;
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
