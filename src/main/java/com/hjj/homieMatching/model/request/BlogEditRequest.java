package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BlogEditRequest implements Serializable {

    private Long id;
    /**
     * 标题
     */
    private String title;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 图片列表
     */
    private List<String> images;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}
