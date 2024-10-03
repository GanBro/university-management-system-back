package com.wubo.universitymanagementsystem.common;

import lombok.Data;

@Data
public class Result<T> {

    private int code; // 状态码，如200表示成功
    private String message; // 返回消息
    private T data; // 返回的数据

    // 成功时调用
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("Success");
        result.setData(data);
        return result;
    }

    // 失败时调用
    public static <T> Result<T> failure(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}
