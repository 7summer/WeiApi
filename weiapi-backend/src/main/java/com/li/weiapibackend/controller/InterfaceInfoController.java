package com.li.weiapibackend.controller;

import com.li.weiapibackend.common.BaseResponse;
import com.li.weiapibackend.common.ErrorCode;
import com.li.weiapibackend.common.IdRequest;
import com.li.weiapibackend.constant.InterfaceInfoConstant;
import com.li.weiapibackend.exception.BusinessException;
import com.li.weiapibackend.model.VO.AnalysisInterfaceVO;
import com.li.weiapibackend.model.VO.InterfaceInfoQueryResult;
import com.li.weiapibackend.model.VO.UserVO;
import com.li.weiapibackend.model.domain.InterfaceInfo;
import com.li.weiapibackend.model.request.interfaceinfo.InterfaceInfoAddRequest;
import com.li.weiapibackend.model.request.interfaceinfo.InterfaceInfoQueryRequest;
import com.li.weiapibackend.model.request.interfaceinfo.InterfaceInfoUpdateRequest;
import com.li.weiapibackend.model.request.interfaceinfo.InterfaceInvokeRequest;
import com.li.weiapibackend.service.InterfaceInfoService;
import com.li.weiapibackend.service.UserInterfaceService;
import com.li.weiapibackend.service.UserService;
import com.li.weiapibackend.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/interinfo")
@CrossOrigin(origins = {"http://localhost:8000"}, allowCredentials = "true")
public class InterfaceInfoController {
	@Autowired
	private InterfaceInfoService interfaceInfoService;

	@Autowired
	private UserInterfaceService userInterfaceService;

	@Autowired
	private UserService userService;

	/**
	 * 创建接口
	 * @param interfaceInfoAddRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest,
										   HttpServletRequest request) {
		// 鉴权
		userService.isAdmin(request);

		Long id = interfaceInfoService.addInterfaceInfo(interfaceInfoAddRequest);

		return ResultUtils.success(id);
	}

	/**
	 * 删除接口
	 * @param interfaceInfoIdList
	 * @param request
	 * @return
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteInterfaceInfo(@RequestBody List<Long> interfaceInfoIdList,
											  HttpServletRequest request) {
		// 鉴权
		userService.isAdmin(request);

		boolean result = interfaceInfoService.deleteInterfaceInfo(interfaceInfoIdList);

		if (result) return ResultUtils.success(true);
		else throw new BusinessException(ErrorCode.DELETE_INTERFACEINFO_ERROR, "删除接口异常");
	}

	/**
	 * 更新接口
	 * @param interfaceInfoUpdateRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateInterfaceInfo(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
											  HttpServletRequest request) {
		// 鉴权
		userService.isAdmin(request);

		boolean result = interfaceInfoService.updateInterfaceInfo(interfaceInfoUpdateRequest);

		if (result) return ResultUtils.success(true);
		else throw new BusinessException(ErrorCode.UPDATE_INTERFACEINFO_ERROR, "更新接口异常");
	}


	/**
	 * 查询接口
	 * @param interfaceInfoQueryRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/query")
	public BaseResponse<InterfaceInfoQueryResult> queryInterfaceInfo(@RequestBody InterfaceInfoQueryRequest interfaceInfoQueryRequest,
															   HttpServletRequest request) {
		// 登录用户能查询接口
		userService.getCurrent(request);

		InterfaceInfoQueryResult result = interfaceInfoService.queryInterfaceInfo(interfaceInfoQueryRequest);

		return ResultUtils.success(result);
	}

	/**
	 * 获取接口详细
	 * @param idRequest
	 * @return
	 */
	@GetMapping("/description")
	public BaseResponse<InterfaceInfo> getInterfaceInfoById(IdRequest idRequest,
															HttpServletRequest request) {
		// 登录用户能查询接口详细
		userService.getCurrent(request);

		if (idRequest == null || idRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}

		InterfaceInfo interfaceInfo = interfaceInfoService.getInterfaceInfoById(idRequest);

		return ResultUtils.success(interfaceInfo);
	}

	/**
	 * 调用接口
	 * @param interfaceInvokeRequest
	 * @return
	 */
	@GetMapping("/invoke")
	public BaseResponse<Map<String, Object>> invokeInterface(InterfaceInvokeRequest interfaceInvokeRequest,
															 HttpServletRequest request) {
		UserVO currentUser = userService.getCurrent(request);

		interfaceInvokeRequest.setUserId(currentUser.getId());

		Map<String, Object> result = interfaceInfoService.invokeInterface(interfaceInvokeRequest);

		return ResultUtils.success(result);
	}

	/**
	 * 接口分析
	 * @param request
	 * @return
	 */
	@PostMapping("/analysis")
	public BaseResponse<List<AnalysisInterfaceVO>> listAnalysisInterfaceVO(HttpServletRequest request) {
		// 鉴权
		userService.isAdmin(request);

		// 获取每个接口的分析结果
		List<AnalysisInterfaceVO> analysisInterfaceVOList = userInterfaceService.listAnalysisInterfaceVO();

		return ResultUtils.success(analysisInterfaceVOList);
	}

	/**
	 * 接口上线
	 * @param idRequest
	 * @param request
	 * @return
	 */
	@GetMapping("/online")
	public BaseResponse<Boolean> onlineInterface(IdRequest idRequest, HttpServletRequest request) {
		// 鉴权
		userService.isAdmin(request);

		// 上线接口
		boolean result = interfaceInfoService.updateInterfaceInfoStatus(idRequest, InterfaceInfoConstant.ONLINE);

		if (result) return ResultUtils.success(result);
		else throw new BusinessException(ErrorCode.UPDATE_INTERFACEINFO_ERROR, "更新接口状态错误");
	}

	/**
	 * 接口下线
	 * @param idRequest
	 * @param request
	 * @return
	 */
	@GetMapping("/offline")
	public BaseResponse<Boolean> offlineInterface(IdRequest idRequest, HttpServletRequest request) {
		// 鉴权
		userService.isAdmin(request);

		// 下线接口
		boolean result = interfaceInfoService.updateInterfaceInfoStatus(idRequest, InterfaceInfoConstant.OFFLINE);

		if (result) return ResultUtils.success(result);
		else throw new BusinessException(ErrorCode.UPDATE_INTERFACEINFO_ERROR, "更新接口状态错误");
	}
}
