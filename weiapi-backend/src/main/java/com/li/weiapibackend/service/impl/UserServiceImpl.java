package com.li.weiapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li.weiapibackend.common.ErrorCode;
import com.li.weiapibackend.constant.UserConstant;
import com.li.weiapibackend.exception.BusinessException;
import com.li.weiapibackend.mapper.IdentifyMapper;
import com.li.weiapibackend.model.VO.UserVO;
import com.li.weiapibackend.model.domain.Identify;
import com.li.weiapibackend.model.domain.Keys;
import com.li.weiapibackend.model.domain.User;
import com.li.weiapibackend.model.request.user.*;
import com.li.weiapibackend.service.UserService;
import com.li.weiapibackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
* @author 33278
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-09-03 16:13:58
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 盐值（混淆密码）
     */
    private static final String SALT = "quake";

    @Resource
    private UserMapper userMapper;

    @Resource
    private IdentifyMapper identifyMapper;

    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 用户编号
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "账户、密码、二次密码不能为空");
        }
        if (userAccount.length() < 4 || userAccount.length() >= 255) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度需要>=4");
        }
        if (userPassword.length() < 8 || userPassword.length() >= 255
                || checkPassword.length() < 8 || checkPassword.length() >= 255) {

            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度需要>=8");
        }

        //账号不包含特殊字符
        if (!userAccount.matches("^[a-z0-99A-Z][a-z0-9A-Z_]+[a-z0-99A-Z]$")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不能包含特殊字符");
        }
        //密码和二次密码相同
        if(!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和二次密码不同");
        }
        //账户不能重复
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("userAccount", userAccount);
        long count = this.count(wrapper);
        if(count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户重复");
        }

        //搅屎棍，让加密后的密码更加复杂
        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());

        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);

        boolean saveResult = this.save(user);

        if (saveResult == false) {
            throw new BusinessException(ErrorCode.REGISTER_ERROR, "注册失败");
        }

        return user.getId();
    }

    /**
     * 用户登录
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱密后的User对象
     */
    @Override
    public UserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "账户、密码不能为空");
        }
        if (userAccount.length() < 4 || userAccount.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度范围4-20");
        }
        if (userPassword.length() < 8 || userPassword.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度范围8-30");
        }

        if (!userAccount.matches("^[a-z0-99A-Z][a-z0-9A-Z_]+[a-z0-99A-Z]$")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不能包含特殊字符");
        }

        //加密
        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());

        QueryWrapper<User> wrapper = new QueryWrapper<User>();
        wrapper.eq("userAccount", userAccount);
        wrapper.eq("userPassword", encryptPassword);

        User user = userMapper.selectOne(wrapper);
        //用户不存在
        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_ERROR, "账户或密码错误");
        }

        UserVO safetyUser = getSafetyUser(user);

        //记录用户的登录态
        HttpSession session = request.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATUS, safetyUser);
        // 默认时间：600s
        session.setMaxInactiveInterval(600);

        return safetyUser;
    }

    /**
     * 获取登录用户
     * @param request
     * @return
     */
    @Override
    public UserVO getCurrent(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        UserVO currentUser = (UserVO) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }

        long userId = currentUser.getId();
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法获取当前登录的用户");
        }

        UserVO safetyUser = getSafetyUser(user);
        return safetyUser;
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    @Override
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
     * 查询用户（返回脱敏用户）
     * @param searchUserRequest
     * @return
     */
    @Override
    public SearchUserResult searchUser(SearchUserRequest searchUserRequest) {
        // 查询所有数据时，searchUserRequest不为空
        // username为空 userStatus为空 userRole为空
        if (searchUserRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String username = searchUserRequest.getUsername();
        if (username != null && username.trim().length() == 0) {
            searchUserRequest.setUsername(null);
        }

        Integer pageIndex = searchUserRequest.getPageIndex();
        if (pageIndex == null || pageIndex <= 0) {
            pageIndex = UserConstant.pageIndex;
        }
        Integer pageSize = searchUserRequest.getPageSize();
        if (pageSize == null || pageSize > UserConstant.maxPageSize || pageSize <= 0) {
            pageSize = UserConstant.maxPageSize;
        }

        List<User> userList = userMapper.searchUser(searchUserRequest, (pageIndex-1)*pageSize, pageSize);
        List<UserVO> safeUserList = userList.stream().map(user -> getSafetyUser(user)).collect(Collectors.toList());
        long total = this.count();

        SearchUserResult result = new SearchUserResult();
        result.setUserVOList(safeUserList);
        result.setTotal(total);

        return result;
    }

    /**
     * 管理员更新用户信息
     * @param updateUserRequest
     * @return
     */
    @Override
    public boolean adminUpdateUser(AdminUpdateUserRequest updateUserRequest) {
        if (updateUserRequest == null || updateUserRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新用户信息异常");
        }

        long id = updateUserRequest.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户编号异常");
        }

        UserVO user = getSafetyUseraById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户编号异常");
        }
        if (user.getUserRole() == UserConstant.ADMIN_ROLE) {
            throw new BusinessException(ErrorCode.DELETE_USER_ERROR, "无法更新管理员信息");
        }

        // 密码未加密的user对象
        User updateUser = getUpdateUser(updateUserRequest);
        // 密码加密的user对象
        User encryUser = getEncryptUser(updateUser);

        return userMapper.updateById(encryUser) > 0;
    }

    /**
     * 更新用户
     * @param updateUserRequest
     * @return
     */
    @Override
    public boolean updateUser(UpdateInfoRequest updateUserRequest) {
        // updateUserRequest的属性都为空
        if (updateUserRequest == null || updateUserRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新用户信息异常");
        }

        long id = updateUserRequest.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.UPDATE_USER_ERROR, "用户编号异常");
        }

        UserVO user = getSafetyUseraById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户编号异常");
        }

        // 密码未加密的user对象
        User updateUser = getUpdateUser(updateUserRequest);
        // 密码加密的user对象
        User encryUser = getEncryptUser(updateUser);

        return userMapper.updateById(encryUser) > 0;
    }

    /**
     * 更新用户密码
     * @param updateUserRequest
     * @return
     */
    @Override
    public boolean updatePassword(UpdatePasswordRequest updateUserRequest) {
        if (updateUserRequest == null || updateUserRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新用户信息异常");
        }

        long id = updateUserRequest.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.UPDATE_USER_ERROR, "用户编号异常");
        }

        // 获得加密的旧密码
        String oldPassword = updateUserRequest.getOldPassword();
        String encryptOldPassword = null;
        if (oldPassword == null || oldPassword.length() < 8 || oldPassword.length() >= 255) {
            throw new BusinessException(ErrorCode.UPDATE_USER_ERROR, "旧密码格式错误");
        } else {
            encryptOldPassword = getEncryptPassword(oldPassword);
        }

        // 获得新密码
        String newPassword = updateUserRequest.getNewPassword();
        String encryptNewPassword = null;
        if (newPassword == null || newPassword.length() < 8 || newPassword.length() >= 255) {
            throw new BusinessException(ErrorCode.UPDATE_USER_ERROR, "新密码格式错误");
        } else {
            encryptNewPassword = getEncryptPassword(newPassword);
        }
        if (encryptOldPassword.equals(encryptNewPassword)) {
            throw new BusinessException(ErrorCode.UPDATE_USER_ERROR, "新密码与旧密码相同");
        }

        // 返回带加密密码的user对象
        User user = getUserById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.UPDATE_USER_ERROR, "用户编号异常");
        }
        if (user.getUserPassword().equals(encryptOldPassword) == false) {
            throw new BusinessException(ErrorCode.UPDATE_USER_ERROR, "旧密码错误");
        }

        // 密码未加密的user对象
        User updateUser = getUpdateUser(updateUserRequest);
        // 密码加密的user对象
        User encryUser = getEncryptUser(updateUser);

        return userMapper.updateById(encryUser) > 0;
    }

    /**
     * 管理员删除用户信息
     * @param deleteUserRequest
     * @return
     */
    @Override
    public boolean deleteUer(DeleteUserRequest deleteUserRequest) {
        if (deleteUserRequest == null || deleteUserRequest.getId() == null) {
            throw new BusinessException(ErrorCode.DELETE_USER_ERROR, "删除用户信息异常");
        }

        long id = deleteUserRequest.getId();
        UserVO user = getSafetyUseraById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.DELETE_USER_ERROR, "用户编号异常");
        }
        if (user.getUserRole() == UserConstant.ADMIN_ROLE) {
            throw new BusinessException(ErrorCode.DELETE_USER_ERROR, "不能删除管理员信息");
        }

        return userMapper.deleteById(id) > 0;
    }

    /**
     * 用户信息脱敏
     * @param originUser
     * @return 脱密后的User对象
     */
    public UserVO getSafetyUser(User originUser) {
        if (originUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        //用户信息脱敏
        UserVO safetyUser = new UserVO();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setDescription(originUser.getDescription());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setCreateTime(originUser.getCreateTime());

        return safetyUser;
    }

    /**
     * 通过id返回user对象（脱敏）
     * @param id
     * @return
     */
    @Override
    public UserVO getSafetyUseraById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户编号不存在");
        }

        User user = userMapper.selectById(id);

        return getSafetyUser(user);
    }

    /**
     * 通过id返回user对象（带加密密码的用户对象）
     * @param id
     * @return
     */
    @Override
    public User getUserById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户编号不存在");
        }

        User user = userMapper.selectById(id);

        return user;
    }

    /**
     * 得到密码加密的user对象
     * @return
     */
    @Override
    public User getEncryptUser(User user) {
        // 获得未加密的密码
        String userPassword = user.getUserPassword();
        // 获得加密密码
        String encryptPassword = getEncryptPassword(userPassword);

        user.setUserPassword(encryptPassword);

        return user;
    }

    /**
     * 对密码进行加密
     * @param password
     * @return
     */
    public String getEncryptPassword(String password) {
        String encryptPassword = null;
        if (password != null && password.length() >= 8 && password.length() < 255) {
            encryptPassword = DigestUtils.md5DigestAsHex((password + SALT).getBytes());
        }

        return encryptPassword;
    }

    /**
     * 将UpdateUserRequest转化为User（密码未加密）
     * @param updateUserRequest
     * @return
     */
    @Override
    public User getUpdateUser(AdminUpdateUserRequest updateUserRequest) {
        //向数据库更新用户数据
        User updateUser = new User();
        updateUser.setId(updateUserRequest.getId());

        String username = updateUserRequest.getUsername();
        if (StringUtils.isEmpty(username) || username.length() >= 255) {
            username = null;
        }
        updateUser.setUsername(username);

        String avatarUrl = updateUserRequest.getAvatarUrl();
        if (StringUtils.isEmpty(avatarUrl) || avatarUrl.length() >= 1024) {
            avatarUrl = null;
        }
        updateUser.setAvatarUrl(avatarUrl);

        String email = updateUserRequest.getEmail();
        if (StringUtils.isEmpty(email) || email.length() >= 255) {
            email = null;
        }
        updateUser.setEmail(email);

        String password = updateUser.getUserPassword();
        if (StringUtils.isEmpty(password) || password.length() >= 255) {
            password = null;
        }
        updateUser.setUserPassword(password);

        updateUser.setUserStatus(updateUserRequest.getUserStatus());

        return updateUser;
    }

    /**
     * 将updateInfoRequest转化为User（没有密码）
     * @param updateInfoRequest
     * @return
     */
    @Override
    public User getUpdateUser(UpdateInfoRequest updateInfoRequest) {
        User updateUser = new User();

        updateUser.setId(updateInfoRequest.getId());

        String username = updateInfoRequest.getUsername();
        if (StringUtils.isEmpty(username) || username.length() >= 255) {
            username = null;
        }
        updateUser.setUsername(username);

        String email = updateInfoRequest.getEmail();
        if (StringUtils.isEmpty(email) || email.length() >= 255) {
            email = null;
        }
        updateUser.setEmail(email);

        String avatarUrl = updateInfoRequest.getAvatarUrl();
        if (StringUtils.isEmpty(avatarUrl) || avatarUrl.length() >= 1024) {
            avatarUrl = null;
        }
        updateUser.setAvatarUrl(avatarUrl);

        return updateUser;
    }

    /**
     * 将UpdatePasswordRequest转化为User（密码未加密）
     * @param updatePasswordRequest
     * @return
     */
    public User getUpdateUser(UpdatePasswordRequest updatePasswordRequest) {
        User updateUser = new User();
        updateUser.setId(updatePasswordRequest.getId());

        String newPassword = updatePasswordRequest.getNewPassword();
        if (StringUtils.isEmpty(newPassword) || newPassword.length() >= 255) {
            newPassword = null;
        }
        updateUser.setUserPassword(newPassword);

        return updateUser;
    }

    /**
     * 生成密钥
     * @param request
     * @return
     */
    @Override
    public boolean generateKeys(HttpServletRequest request) {
        UserVO userVO = getCurrent(request);
        User user = getUserById(userVO.getId());

        Identify identify = identifyMapper.selectById(user.getId());

        Random random = new Random();

        String accessKey = DigestUtils.md5DigestAsHex((SALT + random.nextInt(1000) + user.getUserAccount()).getBytes());
        String secretKey = DigestUtils.md5DigestAsHex((SALT + random.nextInt(1000) + String.valueOf(System.currentTimeMillis()) + user.getUserPassword()).getBytes());
        if (identify == null) {
            Identify addIdentity = new Identify();
            addIdentity.setId(user.getId());
            addIdentity.setAccessKey(accessKey);
            addIdentity.setSecretKey(secretKey);

            return identifyMapper.insert(addIdentity) > 0;
        } else {
            identify.setAccessKey(accessKey);
            identify.setSecretKey(secretKey);

            return identifyMapper.updateById(identify) > 0;
        }
    }
}




