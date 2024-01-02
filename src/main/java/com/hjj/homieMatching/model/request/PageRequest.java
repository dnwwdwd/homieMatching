package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求参数
 */
@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID =-5860707094194210842L;
    /**
     * 每页多少条数据
     */
    protected int pageSize;
    /**
     * 当前的页数
     */
    protected int pageNum;
}
