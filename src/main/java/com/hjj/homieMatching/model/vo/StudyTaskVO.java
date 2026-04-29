package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class StudyTaskVO implements Serializable {

    private Long id;

    private Long userId;

    private String title;

    private String description;

    private Integer targetTime;

    private Date taskDate;

    private Integer status;

    private Long likeNum;

    private Integer checkinCount;

    private Date createTime;

    private Date updateTime;

    private StudyTaskUserVO taskUserVO;

    private boolean isLiked;

    private List<StudyCheckinVO> checkinVOList;

    private static final long serialVersionUID = 1L;

    public void setIsLiked(boolean liked) {
        isLiked = liked;
    }
}
