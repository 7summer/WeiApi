package com.li.weiapibackend.model.request.user;

import com.li.weiapibackend.model.VO.UserVO;
import lombok.Data;

import java.util.List;

@Data
public class SearchUserResult {
	private List<UserVO> userVOList;
	private long total;
}
