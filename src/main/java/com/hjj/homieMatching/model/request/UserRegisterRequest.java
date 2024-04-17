package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户注册请求体
 * @author hjj
 */
@Data
public class UserRegisterRequest implements Serializable {
<<<<<<< HEAD

    private static final long serialVersionUID=3191241716373120793L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String planetCode;

    private Integer gender;

    private String avatarUrl;

    private String username;

    private String phone;

    private List<String> tagNameList;

    private Double longitude;

=======
    private static final long serialVersionUID=3191241716373120793L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
    private Integer gender;
    private String avatarUrl;
    private String username;
    private String phone;
    private List<String> tagNameList;
    private Double longitude;
>>>>>>> 55c2d1b2d36429f4d11db279a9446ead320cb342
    private Double dimension;
}
