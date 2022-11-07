package com.shijivk.todokireader.pojo;

public class Result {
    private Object data;
    private String msg;

    public static Result fail(String msg){
        return new Result(null,msg);
    }

    public static Result success(Object data){
        return new Result(data,null);
    }

    public Result(Object data, String msg) {
        this.data = data;
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
