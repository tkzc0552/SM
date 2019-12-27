package com.zhm.dto;

/**
 * 校验Model(是否需要跳转到新密码页面)
 * Created by 赵红明 on 2019/7/1.
 */
public class CheckUserDto {
    private String userName;
    private String code;
    private String email;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
