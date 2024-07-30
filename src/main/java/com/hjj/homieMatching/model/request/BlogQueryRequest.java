package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class BlogQueryRequest extends PageRequest implements Serializable {
    private String title;
    private String userId;
    Integer type; // 查询类型（0 查自己收藏的，1 查自己点赞的，2 查别人收藏的，3 查别人点赞的 4 查自己的阅读过得文章）
}
