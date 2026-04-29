package com.hjj.homieMatching.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value = "study_checkin")
@Data
public class StudyCheckin implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;

    private Long userId;

    private Date checkinTime;

    private Integer actualTime;

    private String note;

    private String images;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
