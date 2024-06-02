package com.li.weiapibackend.mapper;

import com.li.weiapibackend.model.VO.AnalysisInterfaceVO;
import com.li.weiapibackend.model.domain.UserInterface;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 33278
* @description 针对表【user_interface】的数据库操作Mapper
* @createDate 2024-03-26 14:52:50
* @Entity com.li.weiapibackend.model.domain.UserInterface
*/
public interface UserInterfaceMapper extends BaseMapper<UserInterface> {
	List<AnalysisInterfaceVO> listAnalysisInterfaceVO();
}




