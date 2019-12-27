package com.zhm.dto;

/**
 * 发送邮件DTO
 * Created by 赵红明 on 2019/6/28.
 */
public class SendEmailDto {
    private String toEmail;
    private String userName;

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
