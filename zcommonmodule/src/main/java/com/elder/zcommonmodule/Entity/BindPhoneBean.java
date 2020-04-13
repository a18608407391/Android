package com.elder.zcommonmodule.Entity;

public class BindPhoneBean {
    //{"code":0,"data":"7885f10a-8318-4f3e-aa2b-83b309a2ecca","msg":"成功"}
    private int code;
    private String data;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
