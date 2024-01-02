package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 * @author hjj
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID=3191241716373120793L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
}
