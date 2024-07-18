package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BlogVO implements Serializable{
    /**
     * id
     */
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
    private String images;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private String tags;

    /**
     * 浏览量
     */
    private Integer viewNum;

    /**
     * 点赞数
     */
    private Integer likeNum;

    /**
     * 收藏数
     */
    private Integer starNum;

    /**
     * 评论数
     */
    private Integer commentNum;

    /**
     * 作者名称
     */
    private BlogUserVO blogUserVO;

    /**
     * 创建时间
     */
    private String createTime;

    private static final long serialVersionUID = 1L;
}
