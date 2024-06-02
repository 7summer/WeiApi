package com.li.weiapibackend.dubbo;

import com.li.weiapibackend.service.UserInterfaceService;
import com.li.weiapicommon.dubbo.InnerUserInterfaceService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@DubboService
@Component
public class InnerUserInterfaceServiceImpl implements InnerUserInterfaceService {
	@Autowired
	private UserInterfaceService userInterfaceService;

	@Override
	public boolean updateCount(Long userId, Long interfaceId) {
		return userInterfaceService.updateCount(userId, interfaceId);
	}

	@Override
	public boolean canInvoke(Long userId, Long interfaceId) {
		return userInterfaceService.canInvoke(userId, interfaceId);
	}
}
