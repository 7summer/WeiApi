package com.li.weiapicommon.dubbo;

public interface InnerUserInterfaceService {
	/**
	 * 根据用户编号和接口编号更新次数
	 * @param userId
	 * @param interfaceId
	 * @return
	 */
	boolean updateCount(Long userId, Long interfaceId);

	/**
	 * 验证用户可以调用接口
	 * @param userId
	 * @param interfaceId
	 * @return
	 */
	boolean canInvoke(Long userId, Long interfaceId);
}
