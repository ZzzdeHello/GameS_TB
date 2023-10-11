package com.zzzde.game.tb.common;

import jdk.nashorn.internal.objects.annotations.Getter;

/**
 * 回复参数
 * @Author zzzde
 * @Date 2023/10/10
 */
public enum ResponseEnum {
    /**
     * 请求编码以及说明
     */
    OK(0,"请求成功"),
    FAIL(1,"服务异常"),
    LIMIT(11,"请求访问限制"),
    USER_FAILED(21,"用户访问限制"),
    ;
    private Integer code;

    private String message;

    ResponseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
