package com.hjj.homieMatching.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hjj.homieMatching.model.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 队伍查询封装类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * id 列表
     */
    private List<Long> idList;
    /**
     * 搜索关键词（同时对队伍名称和描述搜索）
     */
    private String searchText;
    /**
     * 队伍名称
     */
    private String teamName;

    /**
     *  描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 队伍创建者id
     */
    private Long userId;

    /**
     * 队伍状态 - 0 - 公开， 1 - 私有，2 - 加密
     */
    private Integer status;
}
