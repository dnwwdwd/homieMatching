package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoVO implements Serializable {

    private Long blogNum;

    private Long followNum;

    private Long starBlogNum;

    private Long likeBlogNum;

    private static final long serialVersionUID = 1L;
}
