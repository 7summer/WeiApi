package com.li.weiapibackend.model.VO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserVO implements Serializable{
	/**
	 * 用户编号
	 */
	private Long id;

	/**
	 * 昵称
	 */
	private String username;

	/**
	 * 用户描述
	 */
	private String description;

	/**
	 * 头像
	 */
	private String avatarUrl;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 用户状态
	 */
	private Integer userStatus;

	/**
	 * 用户角色
	 */
	private Integer userRole;

	/**
	 * 创建时间
	 */
	private Date createTime;
}
