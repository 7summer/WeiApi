package com.li.weiapibackend.model.request.user;

import com.li.weiapibackend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class SearchUserRequest extends PageRequest implements Serializable {
    private String username;
    private Integer userState;
    private Integer userRole;
}
