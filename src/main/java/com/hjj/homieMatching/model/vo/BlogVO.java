package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
    private Long viewNum;

    /**
     * 点赞数
     */
    private Long likeNum;

    /**
     * 收藏数
     */
    private Long starNum;

    /**
     * 评论数
     */
    private Long commentNum;

    /**
     * 评论列表
     */
    private List<CommentVO> commentVOList;

    /**
     * 博客作者 VO
     */
    private BlogUserVO blogUserVO;

    /**
     * 是否点赞
     */
    private boolean isLiked;

    /**
     * 是否收藏
     */
    private boolean isStarred;

    /**
     * 创建时间
     */
    private String createTime;

    private static final long serialVersionUID = 1L;

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public void setIsStarred(boolean isStarred) {
        this.isStarred = isStarred;
    }
}
