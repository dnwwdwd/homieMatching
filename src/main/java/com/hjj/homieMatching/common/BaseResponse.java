package com.hjj.homieMatching.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类，既能返回成功，又能返回错误异常
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {
    private int code;//错误码
    private T data;//前端传来的数据
    private String message;
    private String description;
    public BaseResponse(int code, T data, String message,String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description=description;
    }
    public BaseResponse(int code, T data, String message) {
        this(code,data,message,"");
    }
    public BaseResponse(int code, T data) {
        this(code, data, "","");
    }
    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage(), errorCode.getDescription());
    }
}
