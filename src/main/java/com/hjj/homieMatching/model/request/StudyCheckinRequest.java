package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class StudyCheckinRequest implements Serializable {

    private Long taskId;

    private Integer actualTime;

    private String note;

    private List<String> images;

    private static final long serialVersionUID = 1L;
}
