package com.li.weiapibackend.service;

import com.li.weiapibackend.model.VO.AnalysisInterfaceVO;
import com.li.weiapibackend.model.domain.UserInterface;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 33278
* @description 针对表【user_interface】的数据库操作Service
* @createDate 2024-03-26 14:52:50
*/
public interface UserInterfaceService extends IService<UserInterface> {
	/**
	 * 用户能否调用接口
	 * @param userId
	 * @param interfaceId
	 * @return
	 */
	boolean canInvoke(Long userId, Long interfaceId);

	/**
	 * 用户调用接口次数-1
	 * @param userId
	 * @param interfaceId
	 * @return
	 */
	boolean updateCount(Long userId, Long interfaceId);

	/**
	 * 接口分析
	 * @return
	 */
	List<AnalysisInterfaceVO> listAnalysisInterfaceVO();
}
