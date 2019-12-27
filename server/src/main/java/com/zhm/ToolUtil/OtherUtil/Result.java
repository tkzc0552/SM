package com.zhm.ToolUtil.OtherUtil;

import io.swagger.annotations.ApiModelProperty;

/**
 * 公用返回结果
 * Created by 赵红明 on 2019/6/17.
 */
public class Result<T> {
    /**
     * 返回代码
     */
    @ApiModelProperty(value = "返回代码")
    private String code;
    /**
     * 返回信息
     */
    @ApiModelProperty(value = "返回信息")
    private String message;
    /**
     * 返回详细信息
     */
    @ApiModelProperty(value = "返回详细信息")
    private T data;
    public static Result ok (String msg, Object obj) {
        Result result = new Result();
        result.setCode("00000");
        result.setMessage(msg);
        result.setData(obj);
        return result;
    }

    public static Result failure (String msg) {
        Result result = new Result();
        result.setCode("99999");
        result.setMessage(msg);
        return result;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
