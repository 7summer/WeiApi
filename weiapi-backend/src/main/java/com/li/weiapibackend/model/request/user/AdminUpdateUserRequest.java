package com.li.weiapibackend.model.request.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员更新用户
 */
@Data
public class AdminUpdateUserRequest implements Serializable {
    public static final long serialVersionUID = 425313276L;
    private Long id;
    private String username;
    private String avatarUrl;
    private String email;
    private String userPassword;
    private int userStatus;
}
