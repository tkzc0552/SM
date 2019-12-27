package com.zhm.util;

import java.util.List;

/**
 * Created by 赵红明 on 2019/8/22.
 */
public class ButtonInfo {
    private String optCode;
    private String optCname;
    private String optEname;
    private List<String> urls;

    public String getOptCode() {
        return optCode;
    }

    public void setOptCode(String optCode) {
        this.optCode = optCode;
    }

    public String getOptCname() {
        return optCname;
    }

    public void setOptCname(String optCname) {
        this.optCname = optCname;
    }

    public String getOptEname() {
        return optEname;
    }

    public void setOptEname(String optEname) {
        this.optEname = optEname;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
