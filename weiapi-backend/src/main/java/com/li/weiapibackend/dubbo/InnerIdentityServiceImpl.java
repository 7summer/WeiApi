package com.li.weiapibackend.dubbo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.li.weiapibackend.mapper.IdentifyMapper;
import com.li.weiapibackend.model.domain.Identify;
import com.li.weiapicommon.dubbo.InnerIdentityService;
import com.li.weiapicommon.model.InnerIdentify;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@DubboService
public class InnerIdentityServiceImpl implements InnerIdentityService{
	@Resource
	private IdentifyMapper identifyMapper;

	@Override
	public InnerIdentify getIdentityByUserId(String accessKey) {
		QueryWrapper<Identify> wrapper = new QueryWrapper<>();
		wrapper.eq("accessKey", accessKey);

		Identify identify = identifyMapper.selectOne(wrapper);
		InnerIdentify innerIdentify = new InnerIdentify();
		if (identify != null) {
			BeanUtils.copyProperties(identify, innerIdentify);
		}

		return innerIdentify;
	}
}
