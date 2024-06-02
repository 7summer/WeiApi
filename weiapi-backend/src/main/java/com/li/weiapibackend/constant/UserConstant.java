package com.li.weiapibackend.constant;

public interface UserConstant {
    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATUS = "userLoginStatus";

    //用户角色
    /**
     * 默认用户权限
     */
    int DEFAULT_ROLE = 0;
    /**
     * 管理员权限
     */
    int ADMIN_ROLE = 1;

    /**
     * 默认查询数量
     */
    int pageIndex = 1;
    int pageSize = 4;

    /**
     * 默认最大查询数量
     */
    int maxPageSize = 4;
}
