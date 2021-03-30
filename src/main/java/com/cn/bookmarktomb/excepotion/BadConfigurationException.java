package com.cn.bookmarktomb.excepotion;

import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import lombok.Getter;

/**
 * The exception about config error;
 * @author fallen-angle
 */
@Getter
public class  BadConfigurationException extends RuntimeException {

	private Integer code = ErrorCodeConstant.CONFIGURATION_ERROR_CODE;

	public BadConfigurationException() {
		super();
	}

	public BadConfigurationException(String message) {
		super(message);
	}

	public BadConfigurationException(int code, String message) {
		super(message);
		this.code = code;
	}

}
