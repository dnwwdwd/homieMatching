package com.hjj.homieMatching.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "study_task")
@Data
public class StudyTask implements Serializable {

    @TableId(type = IdType.AUTO)
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

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
