package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class StudyTaskAddRequest implements Serializable {

    private String title;

    private String description;

    private Integer targetTime;

    private Date taskDate;

    private static final long serialVersionUID = 1L;
}
