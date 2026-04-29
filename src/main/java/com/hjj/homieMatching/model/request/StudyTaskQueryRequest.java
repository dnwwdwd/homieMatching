package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class StudyTaskQueryRequest extends PageRequest implements Serializable {

    private Long userId;

    private String title;

    private static final long serialVersionUID = 1L;
}
