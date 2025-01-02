package com.wubo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 与前端交互的数据结构
 *
 * @param <T> 携带数据的类型
 */
@Data
@AllArgsConstructor
public class Result<T> {
    private Integer code;   // 状态码
    private String message; // 返回信息
    private T data;         // 返回数据

    // 默认成功状态码和消息
    private static final Integer SUCCESS_CODE = 200;
    private static final String SUCCESS_MESSAGE = "操作成功";

    // 静态方法 - 默认成功，带数据
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    // 静态方法 - 自定义消息，带数据
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(SUCCESS_CODE, message, data);
    }

    // 静态方法 - 默认失败，带错误码和错误信息
    public static <T> Result<T> error(int resultCode, String message) {
        return new Result<>(resultCode, message, null);
    }

    // 静态方法 - 默认成功，不带数据
    public static <T> Result<T> success() {
        return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }

    // 静态方法 - 默认失败，带错误信息
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
}
