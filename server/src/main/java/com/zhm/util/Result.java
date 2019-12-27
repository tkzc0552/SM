package com.zhm.util;

/**
 * Created by 赵红明 on 2019/6/17.
 */
public class Result<T> {
    /**
     * 代码
     */
    private String code;
    /**
     * 信息
     */
    private String message;


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
