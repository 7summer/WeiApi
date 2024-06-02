package com.li.weiapibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.li.weiapibackend.model.VO.UserVO;
import com.li.weiapibackend.model.domain.User;
import com.li.weiapibackend.model.request.user.*;

import javax.servlet.http.HttpServletRequest;

/**
* @author 33278
* @description 针对表【user】的数据库操作Service
* @createDate 2023-09-03 16:13:58
*/
public interface UserService extends IService<User> {


    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 用户编号
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 返回脱敏后的用户信息
     */
    UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);


    UserVO getCurrent(HttpServletRequest request);

	boolean isAdmin(HttpServletRequest request);

	/**
     * 查询用户
     * @param searchUserRequest
     * @return
     */
    SearchUserResult searchUser(SearchUserRequest searchUserRequest);

    /**
     * 管理员更新用户
     * @param updateUserRequest
     * @return
     */

    boolean adminUpdateUser(AdminUpdateUserRequest updateUserRequest);

    /**
     * 用户更新用户信息
     * @param updateUserRequest
     * @return
     */
    boolean updateUser(UpdateInfoRequest updateUserRequest);

    /**
     * 更新用户密码
     * @param updatePasswordRequest
     * @return
     */
    boolean updatePassword(UpdatePasswordRequest updatePasswordRequest);

    /**
     * 管理员删除用户信息
     * @param deleteUserRequest
     * @return
     */
    boolean deleteUer(DeleteUserRequest deleteUserRequest);

    /**
     * 用户信息脱敏
     * @param originUser
     * @return 脱密后的User对象
     */
    UserVO getSafetyUser(User originUser);

    /**
     * 通过id返回user对象（脱敏）
     * @param id
     * @return
     */
    UserVO getSafetyUseraById(long id);

    /**
     * 通过id返回user对象（带加密密码的用户对象）
     * @param id
     * @return
     */
    User getUserById(long id);

    /**
     * 得到密码加密的user对象
     * @param user
     * @return
     */
    User getEncryptUser(User user);

    /**
     * 将AdminUpdateUserRequest转化为User（密码未加密）
     * @param updateUserRequest
     * @return
     */
    User getUpdateUser(AdminUpdateUserRequest updateUserRequest);

    /**
     * 将UpdateInfoRequest转化为User（没密码）
     * @param updateInfoRequest
     * @return
     */
    User getUpdateUser(UpdateInfoRequest updateInfoRequest);

    /**
     * 生成密钥
     * @param request
     * @return
     */
	boolean generateKeys(HttpServletRequest request);
}
