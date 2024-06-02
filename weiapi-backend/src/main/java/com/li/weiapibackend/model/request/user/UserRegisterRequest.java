package com.li.weiapibackend.model.request.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 76243121L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
