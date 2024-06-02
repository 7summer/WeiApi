package com.li.weiapibackend.mapper;

import com.li.weiapibackend.model.domain.InterfaceInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.li.weiapibackend.model.request.interfaceinfo.InterfaceInfoQueryRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 33278
* @description 针对表【interface_info】的数据库操作Mapper
* @createDate 2024-03-09 15:51:16
* @Entity com.li.weiapibackend.model.domain.InterfaceInfo
*/
public interface InterfaceInfoMapper extends BaseMapper<InterfaceInfo> {
	/**
	 * 查询接口
	 * @param interfaceInfoQueryRequest
	 * @param startIndex
	 * @param pageSize
	 * @return
	 */
	List<InterfaceInfo> queryInterfaceInfo(
			@Param("interfaceInfoQueryRequest") InterfaceInfoQueryRequest interfaceInfoQueryRequest,
			@Param("startIndex") int startIndex,
			@Param("pageSize") int pageSize);

}




