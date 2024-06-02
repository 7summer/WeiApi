package com.li.weiapibackend.dubbo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.li.weiapibackend.mapper.InterfaceInfoMapper;
import com.li.weiapibackend.model.domain.InterfaceInfo;
import com.li.weiapicommon.dubbo.InnerInterfaceInfoService;
import com.li.weiapicommon.model.InnerInterfaceInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {
	@Resource
	private InterfaceInfoMapper interfaceInfoMapper;

	@Override
	public InnerInterfaceInfo getInterfaceInfoByPathAndMethod(String url, String method) {
		QueryWrapper wrapper = new QueryWrapper();
		wrapper.eq("url", url);
		wrapper.eq("method", method);

		InterfaceInfo interfaceInfo = interfaceInfoMapper.selectOne(wrapper);
		InnerInterfaceInfo innerInterfaceInfo = new InnerInterfaceInfo();
		if (interfaceInfo != null) {
			BeanUtils.copyProperties(interfaceInfo, innerInterfaceInfo);
		}

		return innerInterfaceInfo;
	}
}
