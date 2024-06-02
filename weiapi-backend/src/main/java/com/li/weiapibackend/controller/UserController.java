package com.li.weiapibackend.controller;

import com.li.weiapibackend.common.BaseResponse;
import com.li.weiapibackend.common.ErrorCode;
import com.li.weiapibackend.constant.UserConstant;
import com.li.weiapibackend.exception.BusinessException;
import com.li.weiapibackend.model.VO.UserVO;
import com.li.weiapibackend.model.domain.User;
import com.li.weiapibackend.model.request.user.*;
import com.li.weiapibackend.service.impl.UserServiceImpl;
import com.li.weiapibackend.utils.ResultUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://localhost:8000"}, allowCredentials = "true")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return 用户编号
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "userRegisterRequest为空");
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "账户、密码、二次密码不能为空");
        }

        long id = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(id);
    }

    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return 脱敏用户
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "userLoginRequest为空");
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "账户、密码不能为空");
        }

        UserVO user = userService.userLogin(userAccount, userPassword, request);

        return ResultUtils.success(user);
    }

    /**
     * 获取当前登录的用户
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<UserVO> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        UserVO currentUser = (UserVO) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }

        long userId = currentUser.getId();
        User user = userService.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法获取当前登录的用户");
        }

        UserVO safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 退出登录
     * @param request
     * @param request
     */
    @PostMapping("/outLogin")
    public BaseResponse outLogin(HttpServletRequest request) {
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATUS);

        return ResultUtils.success(1);
    }

    /**
     * 用户更新信息
     * @param updateUserRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse updateInformation(@RequestBody UpdateInfoRequest updateUserRequest, HttpServletRequest request) {
        if (updateUserRequest == null || updateUserRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新用户信息异常");
        }

        // 获取当前用户
        UserVO currentUser = getCurrentUser(request).getData();
        // 修改当前用户的信息
        updateUserRequest.setId(currentUser.getId());

        boolean b = userService.updateUser(updateUserRequest);

        if (b) return ResultUtils.success(b);
        else throw new BusinessException(ErrorCode.UPDATE_USER_ERROR, "无法更新");
    }

    /**
     * 用户更新密码
     * @param updatePasswordRequest
     * @param request
     * @return
     */
    @PostMapping("/update/password")
    public BaseResponse updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) {
        if (updatePasswordRequest == null || updatePasswordRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新用户信息异常");
        }

        // 获取当前用户
        UserVO currentUser = getCurrentUser(request).getData();
        // 修改当前用户的密码
        updatePasswordRequest.setId(currentUser.getId());

        boolean b = userService.updatePassword(updatePasswordRequest);

        if (b) return ResultUtils.success(b);
        else throw new BusinessException(ErrorCode.UPDATE_USER_ERROR, "无法更新");
    }

    /**
     * 查询用户
     * @param request
     * @return
     */
    @PostMapping("/admin/search")
    public BaseResponse<SearchUserResult> adminSearchUser(@RequestBody SearchUserRequest searchUserRequest,
                                                          HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }

        SearchUserResult result = userService.searchUser(searchUserRequest);

        return ResultUtils.success(result);
    }

    /**
     * 删除用户
     * @param deleteUserRequest
     * @param request
     * @return
     */
    @PostMapping("/admin/delete")
    public BaseResponse<Boolean> adminDeleteUer(@RequestBody DeleteUserRequest deleteUserRequest, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        boolean b = userService.deleteUer(deleteUserRequest);

        if (b) {
            return ResultUtils.success(b);
        } else {
            throw new BusinessException(ErrorCode.DELETE_USER_ERROR, "无法删除");
        }
    }

    /**
     * 管理员更新用户
     * @param updateUserRequest
     * @return
     */
    @PostMapping("/admin/update")
    public BaseResponse<Boolean> adminUpdateUser(@RequestBody AdminUpdateUserRequest updateUserRequest, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        //管理员更新用户
        boolean b = userService.adminUpdateUser(updateUserRequest);

        if (b) return ResultUtils.success(b);
        else throw new BusinessException(ErrorCode.UPDATE_USER_ERROR, "无法更新");
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        UserVO user = (UserVO) userObj;

        if (user == null ) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        if (user.getUserRole() != UserConstant.ADMIN_ROLE) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        return true;
    }


    /**
     * 生成密钥
     * @param request
     * @return
     */
    @GetMapping("/createKeys")
    public BaseResponse<Boolean> generateKeys(HttpServletRequest request) {
        boolean result = userService.generateKeys(request);

        if (result) {return ResultUtils.success(result);}
        else {
            throw new BusinessException(ErrorCode.GENERATE_KEYS_ERROR, "生成密钥失败");
        }
    }
}
