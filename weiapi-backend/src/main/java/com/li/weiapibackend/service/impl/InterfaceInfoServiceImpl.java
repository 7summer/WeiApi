package com.li.weiapibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.li.weiapibackend.common.ErrorCode;
import com.li.weiapibackend.common.IdRequest;
import com.li.weiapibackend.constant.InterfaceInfoConstant;
import com.li.weiapibackend.exception.BusinessException;
import com.li.weiapibackend.mapper.IdentifyMapper;
import com.li.weiapibackend.model.VO.InterfaceInfoQueryResult;
import com.li.weiapibackend.model.domain.Identify;
import com.li.weiapibackend.model.domain.InterfaceInfo;
import com.li.weiapibackend.model.request.interfaceinfo.InterfaceInfoAddRequest;
import com.li.weiapibackend.model.request.interfaceinfo.InterfaceInfoQueryRequest;
import com.li.weiapibackend.model.request.interfaceinfo.InterfaceInfoUpdateRequest;
import com.li.weiapibackend.model.request.interfaceinfo.InterfaceInvokeRequest;
import com.li.weiapibackend.service.InterfaceInfoService;
import com.li.weiapibackend.mapper.InterfaceInfoMapper;
import com.li.weiapibackend.service.UserInterfaceService;
import com.li.weiapiclientsdk.client.WeiApiClient;
import com.li.weiapiclientsdk.common.UserInvokeInterfaceRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
* @author 33278
* @description 针对表【interface_info】的数据库操作Service实现
* @createDate 2024-03-09 15:51:16
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService{
	@Resource
	private InterfaceInfoMapper interfaceInfoMapper;

	@Resource
	private IdentifyMapper identifyMapper;

	@Autowired
	private UserInterfaceService userInterfaceService;

	/**
	 * 添加接口
	 * @param interfaceInfoAddRequest
	 * @return
	 */
	@Override
	public Long addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest) {
		if (interfaceInfoAddRequest == null) {
			throw new BusinessException(ErrorCode.NULL_ERROR);
		}

		InterfaceInfo interfaceInfo = interfaceInfoAddRequestInvertInterfaceInfo(interfaceInfoAddRequest);
		if (generalValidateInterfaceInfo(interfaceInfo)) {
			interfaceInfoMapper.insert(interfaceInfo);
		}

		return interfaceInfo.getId();
	}

	/**
	 * 删除接口
	 * @param interfaceInfoIdList
	 * @return
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteInterfaceInfo(List<Long> interfaceInfoIdList) {
		if (CollectionUtils.isEmpty(interfaceInfoIdList)) {
			return true;
		}

		int count = interfaceInfoMapper.deleteBatchIds(interfaceInfoIdList);
		if (count == interfaceInfoIdList.size()) {
			return true;
		} else {
			throw new BusinessException(ErrorCode.DELETE_INTERFACEINFO_ERROR, "删除接口异常");
		}
	}

	/**
	 * 更新接口
	 * @param interfaceInfoUpdateRequest
	 * @return
	 */
	@Override
	@Transactional
	public boolean updateInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
		if (interfaceInfoUpdateRequest == null) {
			throw new BusinessException(ErrorCode.NULL_ERROR);
		}

		Long id = interfaceInfoUpdateRequest.getId();
		if (id == null || id <= 0) {
			throw new BusinessException(ErrorCode.UPDATE_INTERFACEINFO_ERROR, "接口编号异常");
		}

		InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(id);
		if (interfaceInfo == null) {
			throw new BusinessException(ErrorCode.UPDATE_INTERFACEINFO_ERROR, "接口编号异常");
		}

		InterfaceInfo updateInterfaceInfo = interfaceInfoUpdateRequestInvertInterfaceInfo(interfaceInfoUpdateRequest);
		if (generalValidateInterfaceInfo(updateInterfaceInfo)) {
			int count = interfaceInfoMapper.updateById(updateInterfaceInfo);
			if (count <= 0) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 查询接口
	 * @param interfaceInfoQueryRequest
	 * @return
	 */
	@Override
	public InterfaceInfoQueryResult queryInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
		if (interfaceInfoQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}

		String interfaceName = interfaceInfoQueryRequest.getInterfaceName();
		if (StringUtils.isEmpty(interfaceName)) {
			interfaceInfoQueryRequest.setInterfaceName(null);
		}

		String description = interfaceInfoQueryRequest.getDescription();
		if (StringUtils.isEmpty(description)) {
			interfaceInfoQueryRequest.setDescription(null);
		}

		String url = interfaceInfoQueryRequest.getUrl();
		if (StringUtils.isEmpty(url)) {
			interfaceInfoQueryRequest.setUrl(null);
		}

		String method = interfaceInfoQueryRequest.getMethod();
		if (StringUtils.isEmpty(method)) {
			interfaceInfoQueryRequest.setMethod(null);
		}

		Integer pageIndex = interfaceInfoQueryRequest.getPageIndex();
		if (pageIndex == null || pageIndex <= 0) {
			interfaceInfoQueryRequest.setPageIndex(InterfaceInfoConstant.pageIndex);
		}

		Integer pageSize = interfaceInfoQueryRequest.getPageSize();
		if (pageSize == null || pageSize > InterfaceInfoConstant.maxPageSize || pageSize <= 0) {
			interfaceInfoQueryRequest.setPageSize(InterfaceInfoConstant.pageSize);
		}

		List<InterfaceInfo> interfaceInfoList = interfaceInfoMapper.queryInterfaceInfo(
				interfaceInfoQueryRequest,
				(interfaceInfoQueryRequest.getPageIndex()-1)*interfaceInfoQueryRequest.getPageSize(),
				interfaceInfoQueryRequest.getPageSize());
		long total = this.count();

		InterfaceInfoQueryResult result = getInterfaceInfoQueryResult(interfaceInfoList, total);

		return result;
	}

	/**
	 * 通过id返回接口信息
	 * @param idRequest
	 * @return
	 */
	@Override
	public InterfaceInfo getInterfaceInfoById(IdRequest idRequest) {
		if (idRequest == null || idRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}

		long id = idRequest.getId();

		InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(id);
		if (interfaceInfo == null ) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口编号异常");
		}

		return interfaceInfo;
	}


	/**
	 * 更新接口状态
	 * @param idRequest
	 * @return
	 */
	@Override
	public boolean updateInterfaceInfoStatus(IdRequest idRequest, int status) {
		if (idRequest == null || idRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}

		InterfaceInfo interfaceInfo = getInterfaceInfoById(idRequest);
		if (interfaceInfo.getInterfaceStatus().equals(status)) {
			return true;
		}

		UpdateWrapper wrapper = new UpdateWrapper();
		wrapper.eq("id", interfaceInfo.getId());
		wrapper.set("interfaceStatus", status);

		boolean update = this.update(wrapper);

		return update;
	}

	/**
	 * 调用接口
	 *
	 * @param interfaceInvokeRequest
	 * @return
	 */
	@Override
	public Map<String, Object> invokeInterface(InterfaceInvokeRequest interfaceInvokeRequest) {
		Long userId = interfaceInvokeRequest.getUserId();
		Long interfaceId = interfaceInvokeRequest.getInterfaceId();

		InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(interfaceId);
		if (interfaceInfo == null) {
			throw new BusinessException(ErrorCode.INVOKE_INTERFACEINFO_ERROR, "执行接口错误");
		}

		// 判断用户能否调用接口
		if (!userInterfaceService.canInvoke(userId, interfaceId)) {
			throw new BusinessException(ErrorCode.INVOKE_INTERFACEINFO_ERROR, "执行接口错误");
		}

		// 获取用户的accessKey和secretKey
		Identify identify = identifyMapper.selectById(interfaceInvokeRequest.getUserId());
		if (identify == null) {
			throw new BusinessException(ErrorCode.INVOKE_INTERFACEINFO_ERROR, "执行接口错误");
		}
		String accessKey = identify.getAccessKey();
		String secretKey = identify.getSecretKey();

		UserInvokeInterfaceRequest userInvokeInterfaceRequest = invertToUserInvokeInterfaceRequest(interfaceInfo, interfaceInvokeRequest);

		WeiApiClient weiApiClient = new WeiApiClient(accessKey, secretKey);
		Map<String, Object> result = null;
		try {
			result = weiApiClient.generalInvokeInterface(userInvokeInterfaceRequest);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.INVOKE_INTERFACEINFO_ERROR, "执行接口错误");
		}

		return result;
	}

	/**
	 * 将InterfaceInfo和InterfaceInvokeRequest转化成UserInvokeInterfaceRequest
	 * @param interfaceInfo
	 * @param interfaceInvokeRequest
	 * @return
	 */
	private UserInvokeInterfaceRequest invertToUserInvokeInterfaceRequest(InterfaceInfo interfaceInfo, InterfaceInvokeRequest interfaceInvokeRequest) {
		UserInvokeInterfaceRequest userInvokeInterfaceRequest = new UserInvokeInterfaceRequest();
		userInvokeInterfaceRequest.setId(interfaceInfo.getId());
		userInvokeInterfaceRequest.setInterfaceName(interfaceInfo.getInterfaceName());
		userInvokeInterfaceRequest.setUrl(interfaceInfo.getUrl());
		userInvokeInterfaceRequest.setMethod(interfaceInfo.getMethod());
		userInvokeInterfaceRequest.setUserParams(interfaceInvokeRequest.getUserParams());

		return userInvokeInterfaceRequest;
	}

	/**
	 * 创建接口和更新接口的通用验证方法
	 * @param interfaceInfo
	 * @return
	 */
	private boolean generalValidateInterfaceInfo(InterfaceInfo interfaceInfo) {
		String url = interfaceInfo.getUrl();
		if (url == null || StringUtils.isEmpty(url.trim())) {
			throw new BusinessException(ErrorCode.INSERT_INTERFACEINFO_ERROR, "接口地址不为空");
		}

		String requestHeader = interfaceInfo.getRequestHeader();
		if (requestHeader == null || StringUtils.isEmpty(requestHeader.trim())) {
			throw new BusinessException(ErrorCode.INSERT_INTERFACEINFO_ERROR, "请求头不为空");
		}

		String responseHeader = interfaceInfo.getResponseHeader();
		if (responseHeader == null || StringUtils.isEmpty(responseHeader.trim())) {
			throw new BusinessException(ErrorCode.INSERT_INTERFACEINFO_ERROR, "响应头不为空");
		}

		String method = interfaceInfo.getMethod();
		if (method == null || StringUtils.isEmpty(method.trim())) {
			throw new BusinessException(ErrorCode.INSERT_INTERFACEINFO_ERROR, "请求类型不为空");
		}
		if (InterfaceInfoConstant.methodList.contains(method) == false) {
			throw new BusinessException(ErrorCode.INSERT_INTERFACEINFO_ERROR, "请求类型异常");
		}

		String params = interfaceInfo.getParams();
		if (params == null || StringUtils.isEmpty(params.trim())) {
			throw new BusinessException(ErrorCode.INSERT_INTERFACEINFO_ERROR, "请求参数不为空");
		}

		Integer interfaceInfoStatus = interfaceInfo.getInterfaceStatus();
		if (interfaceInfoStatus == null) {
			throw new BusinessException(ErrorCode.INSERT_INTERFACEINFO_ERROR, "接口状态不为空");
		}
		if (InterfaceInfoConstant.interfaceInfoStatusList.contains(interfaceInfoStatus) == false) {
			throw new BusinessException(ErrorCode.INSERT_INTERFACEINFO_ERROR, "接口状态异常");
		}

		return true;
	}

	/**
	 * 将interfaceInfoList和total封装为接口查找结果类
	 * @param interfaceInfoList
	 * @param total
	 * @return
	 */
	private InterfaceInfoQueryResult getInterfaceInfoQueryResult(List<InterfaceInfo> interfaceInfoList, long total) {
		InterfaceInfoQueryResult result = new InterfaceInfoQueryResult();
		result.setInterfaceInfoList(interfaceInfoList);
		result.setTotal(total);

		return result;
	}

	/**
	 * 将InterfaceInfoAddRequest转化为InterfaceInfo
	 * @param interfaceInfoAddRequest
	 * @return
	 */
	private InterfaceInfo interfaceInfoAddRequestInvertInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest) {
		InterfaceInfo interfaceInfo = new InterfaceInfo();

		BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);

		return interfaceInfo;
	}

	/**
	 * 将InterfaceInfoUpdateRequest转化为InterfaceInfo
	 * @param interfaceInfoUpdateRequest
	 * @return
	 */
	private InterfaceInfo interfaceInfoUpdateRequestInvertInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest) {
		InterfaceInfo interfaceInfo = new InterfaceInfo();

		BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);

		return interfaceInfo;
	}
}




