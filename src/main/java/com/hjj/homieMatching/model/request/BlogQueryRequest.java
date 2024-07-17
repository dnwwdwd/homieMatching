package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class BlogQueryRequest extends PageRequest implements Serializable {
    private String title;
}
