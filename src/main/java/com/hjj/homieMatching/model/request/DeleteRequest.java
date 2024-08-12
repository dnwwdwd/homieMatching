package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除的请求参数
 */
@Data
public class DeleteRequest implements Serializable {

    private long id;

}
