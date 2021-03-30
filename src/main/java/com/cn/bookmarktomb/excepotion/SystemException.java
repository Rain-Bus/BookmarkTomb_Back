package com.cn.bookmarktomb.excepotion;

import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import lombok.Getter;

/**
 * The exceptions of system appear error;
 * @author fallen-angle
 */
@Getter
public class SystemException extends RuntimeException {

	private Integer code = ErrorCodeConstant.SYSTEM_ERROR_CODE;

	public SystemException(String message) {
		super(message);
}

	public SystemException(int code, String message) {
		super(message);
		this.code = code;
	}
}