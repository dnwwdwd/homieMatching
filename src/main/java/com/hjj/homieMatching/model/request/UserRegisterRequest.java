package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserRegisterRequest implements Serializable {

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

    private Double Dimension;

}
