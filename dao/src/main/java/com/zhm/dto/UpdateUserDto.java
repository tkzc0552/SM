package com.zhm.dto;

/**
 * Created by 赵红明 on 2019/6/27.
 */
public class UpdateUserDto {
    private String userName;
    private String passWord;
    private String repassWord;
    private String conpassWord;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getRepassWord() {
        return repassWord;
    }

    public void setRepassWord(String repassWord) {
        this.repassWord = repassWord;
    }

    public String getConpassWord() {
        return conpassWord;
    }

    public void setConpassWord(String conpassWord) {
        this.conpassWord = conpassWord;
    }
}
