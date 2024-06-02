package com.li.weiapibackend.model.request.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateInfoRequest implements Serializable {
    private Long id;
    private String username;
    private String avatarUrl;
    private String email;
}
