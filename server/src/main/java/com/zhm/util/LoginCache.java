package com.zhm.util;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by 赵红明 on 2019/6/27.
 */
public class LoginCache {
    @ApiModelProperty(value = "用户Id")
    private Integer userId;
    @ApiModelProperty(value = "用户名")
    private String userName;
    @ApiModelProperty(value = "用户头像")
    private String avatarUrl;
    @ApiModelProperty(value = "用户手机号")
    private String mobile;
    @ApiModelProperty(value = "失效时间")
    private String expires;

    public LoginCache(Integer userId,String userName,String mobile,String expires,String avatarUrl){
        this.userId=userId;
        this.userName=userName;
        this.mobile=mobile;
        this.expires=expires;
        this.avatarUrl=avatarUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
