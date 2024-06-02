package com.li.weiapibackend.model.VO;

import com.li.weiapibackend.model.domain.InterfaceInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InterfaceInfoQueryResult implements Serializable {
	private List<InterfaceInfo> interfaceInfoList;
	private Long total;
}
