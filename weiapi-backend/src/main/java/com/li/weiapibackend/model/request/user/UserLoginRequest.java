package com.li.weiapibackend.model.request.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 431513421L;

    private String userAccount;
    private String userPassword;
}
