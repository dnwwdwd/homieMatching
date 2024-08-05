package com.hjj.homieMatching.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 博客表
 * @TableName blog
 */
@TableName(value ="blog")
@Data
public class Blog implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * 作者 id
     */
    private Long userId;

    /**
     * 标签列表
     */
    private String tags;

    /**
     * 浏览数
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}