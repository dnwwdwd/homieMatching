package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class StudyTaskLikeRequest implements Serializable {

    private Long taskId;

    private Boolean isLiked;

    private static final long serialVersionUID = 1L;
}
