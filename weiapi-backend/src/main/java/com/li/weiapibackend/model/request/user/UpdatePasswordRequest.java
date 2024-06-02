package com.li.weiapibackend.model.request.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdatePasswordRequest implements Serializable {
    private Long id;
    private String oldPassword;
    private String newPassword;
}
