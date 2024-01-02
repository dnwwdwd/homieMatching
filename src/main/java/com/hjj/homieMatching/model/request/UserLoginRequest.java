package com.hjj.homieMatching.model.request;

import lombok.Data;

/**
 * 用户登录请求体
 * @autho hjj
 */
@Data
public class UserLoginRequest {
    private static final long serialVersionUID=3191241716373120793L;
    private String userAccount;
    private String userPassword;
}
