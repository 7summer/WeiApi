package com.li.weiapibackend.service;

import com.li.weiapibackend.common.IdRequest;
import com.li.weiapibackend.model.VO.InterfaceInfoQueryResult;
import com.li.weiapibackend.model.domain.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.li.weiapibackend.model.request.interfaceinfo.InterfaceInfoAddRequest;
import com.li.weiapibackend.model.request.interfaceinfo.InterfaceInfoQueryRequest;
import com.li.weiapibackend.model.request.interfaceinfo.InterfaceInfoUpdateRequest;
import com.li.weiapibackend.model.request.interfaceinfo.InterfaceInvokeRequest;

import java.util.List;
import java.util.Map;

/**
* @author 33278
* @description 针对表【interface_info】的数据库操作Service
* @createDate 2024-03-09 15:51:16
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {
	/**
	 * 创建接口
	 * @param interfaceInfoAddRequest
	 * @return
	 */
	Long addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest);

	/**
	 * 删除接口
	 * @param interfaceInfoIdList
	 * @return
	 */
	boolean deleteInterfaceInfo(List<Long> interfaceInfoIdList);

	/**
	 * 更新接口
	 * @param interfaceInfoUpdateRequest
	 * @return
	 */
	boolean updateInterfaceInfo(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest);

	/**
	 * 查询接口
	 * @param interfaceInfoQueryRequest
	 * @return
	 */
	InterfaceInfoQueryResult queryInterfaceInfo(InterfaceInfoQueryRequest interfaceInfoQueryRequest);

	/**
	 * 通过id返回接口信息
	 * @param idRequest
	 * @return
	 */
	InterfaceInfo getInterfaceInfoById(IdRequest idRequest);

	/**
	 * 更新接口状态
	 * @param idRequest
	 * @return
	 */
	boolean updateInterfaceInfoStatus(IdRequest idRequest, int status);

	/**
	 * 调用接口
	 *
	 * @param interfaceInvokeRequest
	 * @return
	 */
	Map<String, Object> invokeInterface(InterfaceInvokeRequest interfaceInvokeRequest);
}
