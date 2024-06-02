package com.li.weiapibackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.li.weiapibackend.model.domain.User;
import com.li.weiapibackend.model.request.user.SearchUserRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 33278
* @description 针对表【user】的数据库操作Mapper
* @createDate 2023-09-03 16:13:58
* @Entity com.li.weiapibackend.model.domain.User
*/
public interface UserMapper extends BaseMapper<User> {
	List<User> searchUser(@Param("searchUserRequest") SearchUserRequest searchUserRequest,
						  @Param("startIndex") int startIndex,
						  @Param("pageSize") int pageSize);
}




