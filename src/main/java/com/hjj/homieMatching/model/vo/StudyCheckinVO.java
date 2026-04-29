package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class StudyCheckinVO implements Serializable {

    private Long id;

    private Long taskId;

    private Long userId;

    private Date checkinTime;

    private Integer actualTime;

    private String note;

    private List<String> images;

    private Date createTime;

    private StudyTaskUserVO checkinUserVO;

    private static final long serialVersionUID = 1L;
}
