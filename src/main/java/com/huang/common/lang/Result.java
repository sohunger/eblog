package com.huang.common.lang;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result implements Serializable {
    //0成功  1失败
    private int status;
    private String msg;
    private Object data;
    private String action;

    public static Result success(String msg, Object data) {
        Result result = new Result();
        result.msg = msg;
        result.data = data;
        result.status = 0;
        return result;
    }

    public static Result success(Object data) {
        return Result.success("操作成功", data);
    }

    public static Result success() {
        Result result = new Result();
        result.msg = "操作成功";
        result.data = null;
        result.status = 0;
        return result;
    }

    public static Result fail(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.status = -1;
        return result;
    }

    public Result action(String action) {
        this.action = action;
        return this;
    }
}
