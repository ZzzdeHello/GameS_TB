package com.zzzde.game.tb.common.dal;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author zzzde
 * @Date 2023/10/10
 */
@Data
public class ResponseDTO<T> implements Serializable {

    private Integer code;

    private String message;

    private T data;
}
