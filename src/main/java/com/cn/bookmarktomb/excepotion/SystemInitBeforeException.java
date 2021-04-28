package com.cn.bookmarktomb.excepotion;

import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;

public class SystemInitBeforeException extends RuntimeException {

	private  Integer code = ErrorCodeConstant.SYSTEM_INIT_BEFORE_CODE;

	public SystemInitBeforeException() {
		super(ErrorCodeConstant.SYSTEM_INIT_BEFORE_MSG);
	}

}
