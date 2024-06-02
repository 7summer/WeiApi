package com.li.weiapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li.weiapibackend.common.ErrorCode;
import com.li.weiapibackend.exception.BusinessException;
import com.li.weiapibackend.mapper.InterfaceInfoMapper;
import com.li.weiapibackend.model.VO.AnalysisInterfaceVO;
import com.li.weiapibackend.model.domain.InterfaceInfo;
import com.li.weiapibackend.model.domain.UserInterface;
import com.li.weiapibackend.service.UserInterfaceService;
import com.li.weiapibackend.mapper.UserInterfaceMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 33278
* @description 针对表【user_interface】的数据库操作Service实现
* @createDate 2024-03-26 14:52:50
*/
@Service
public class UserInterfaceServiceImpl extends ServiceImpl<UserInterfaceMapper, UserInterface>
    implements UserInterfaceService{

	@Resource
	private UserInterfaceMapper userInterfaceMapper;

	@Resource
	private InterfaceInfoMapper interfaceInfoMapper;

	/**
	 * 用户能否调用接口
	 * @param userId
	 * @param interfaceId
	 * @return
	 */
	@Override
	public boolean canInvoke(Long userId, Long interfaceId) {
		if (userId == null || interfaceId == null) {
			throw new BusinessException(ErrorCode.NULL_ERROR, "请求数据错误");
		}

		if (userId <= 0 || interfaceId <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求数据错误");
		}

		QueryWrapper wrapper = new QueryWrapper();
		wrapper.eq("userId", userId);
		wrapper.eq("interfaceId", interfaceId);

		UserInterface userInterface = userInterfaceMapper.selectOne(wrapper);
		if (userInterface == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求数据错误");
		}

		if (userInterface.getLeftNum() <= 0) {
			return false;
		}

		return true;
	}

	/**
	 * 用户调用接口次数-1
	 * @param userId
	 * @param interfaceId
	 * @return
	 */
	@Override
	public boolean updateCount(Long userId, Long interfaceId) {
		if (userId == null || interfaceId == null) {
			throw new BusinessException(ErrorCode.NULL_ERROR, "请求数据错误");
		}

		if (userId <= 0 || interfaceId <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求数据错误");
		}

		UpdateWrapper wrapper = new UpdateWrapper();
		wrapper.eq("userId", userId);
		wrapper.eq("interfaceId", interfaceId);

		wrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");

		return this.update(wrapper);
	}

	/**
	 * 接口分析
	 * @return
	 */
	@Override
	public List<AnalysisInterfaceVO> listAnalysisInterfaceVO() {
		// 从user_interface表中获取接口编号、接口调用总次数
		List<AnalysisInterfaceVO> analysisInterfaceVOList = userInterfaceMapper.listAnalysisInterfaceVO();

		// 获取接口编号
		List<Long> interfaceIdList = analysisInterfaceVOList.stream().map(analysisInterfaceVO -> analysisInterfaceVO.getId())
				.collect(Collectors.toList());
		// 根据接口编号获取接口信息
		List<InterfaceInfo> interfaceInfoList = interfaceInfoMapper.selectBatchIds(interfaceIdList);

		// 补充AnalysisInterfaceVO中的接口信息
		interfaceInfoList.forEach(interfaceInfo -> {
			analysisInterfaceVOList.forEach(analysisInterfaceVO -> {
				if (analysisInterfaceVO.getId().equals(interfaceInfo.getId())) {
					BeanUtils.copyProperties(interfaceInfo, analysisInterfaceVO);
				}
			});
		});

		return analysisInterfaceVOList;
	}
}




