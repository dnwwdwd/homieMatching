package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户创作、点赞、收藏和浏览过的博客 VO
 */
@Data
public class UserBlogVO implements Serializable {

    private BlogUserVO blogUserVO;

    private List<BlogVO> blogVOList;

}
