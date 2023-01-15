package com.lsh.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success(T t) {
        return new Result<>(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.msg, t);
    }

    public static <T> Result<T> failed(String msg) {
        return new Result<>(ResultCodeEnum.FAILED.getCode(), ResultCodeEnum.FAILED.getMsg(), null);
    }

    @Getter
    @AllArgsConstructor
    enum ResultCodeEnum {
        SUCCESS(200, "success"),
        FAILED(400, "failed"),
        ;
        private final Integer code;
        private final String msg;
    }
}
