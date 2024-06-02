package com.li.weiapibackend.model.VO;

import com.li.weiapibackend.model.domain.InterfaceInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口分析类
 */
@Data
public class AnalysisInterfaceVO extends InterfaceInfo implements Serializable {
	/**
	 * 接口调用总次数
	 */
	private Integer totalSum;
}
