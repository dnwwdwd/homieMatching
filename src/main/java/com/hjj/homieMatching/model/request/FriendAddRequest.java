package com.hjj.homieMatching.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class FriendAddRequest implements Serializable {
    private Long friendId;
}
