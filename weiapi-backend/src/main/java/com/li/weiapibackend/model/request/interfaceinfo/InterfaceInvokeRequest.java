package com.li.weiapibackend.model.request.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

@Data
public class InterfaceInvokeRequest implements Serializable {
	private Long interfaceId;
	private Long userId;
	private String userParams;
}
