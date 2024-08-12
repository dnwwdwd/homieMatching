package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserEditRequest implements Serializable {

    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账户
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 用户性别
     */
    private Integer gender;
    /**
     * 用户简介
     */
    private String profile;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 标签列表 json
     */
    private List<String> tags;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 维度
     */
    private Double dimension;

}
