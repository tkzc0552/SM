package com.zhm.entity;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhm.ListenerEntity.ListenerEntity;

import java.util.Date;

/**
 * 系统日志
 *
 * @author zhao.hongming
 * @email 1183483051@qq.com
 * @date 2019-09-11 09:52:06
 */
@Entity
@Table(name = "sys_log")
public class SysLog extends ListenerEntity {


    /**
     *
     */
    private Integer id;


    /**
     * 用户名
     */
    private String username;


    /**
     * 用户操作
     */
    private String operation;


    /**
     * 请求方法
     */
    private String method;


    /**
     * 请求参数
     */
    private String params;


    /**
     * 执行时长(毫秒)
     */
    private Integer time;


    /**
     * IP地址
     */
    private String ip;


    /**
     * 设置：
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取：
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    /**
     * 设置：用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取：用户名
     */
    @Basic
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    /**
     * 设置：用户操作
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * 获取：用户操作
     */
    @Basic
    @Column(name = "operation")
    public String getOperation() {
        return operation;
    }

    /**
     * 设置：请求方法
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * 获取：请求方法
     */
    @Basic
    @Column(name = "method")
    public String getMethod() {
        return method;
    }

    /**
     * 设置：请求参数
     */
    public void setParams(String params) {
        this.params = params;
    }

    /**
     * 获取：请求参数
     */
    @Basic
    @Column(name = "params")
    public String getParams() {
        return params;
    }

    /**
     * 设置：执行时长(毫秒)
     */
    public void setTime(Integer time) {
        this.time = time;
    }

    /**
     * 获取：执行时长(毫秒)
     */
    @Basic
    @Column(name = "time")
    public Integer getTime() {
        return time;
    }

    /**
     * 设置：IP地址
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 获取：IP地址
     */
    @Basic
    @Column(name = "ip")
    public String getIp() {
        return ip;
    }
}
