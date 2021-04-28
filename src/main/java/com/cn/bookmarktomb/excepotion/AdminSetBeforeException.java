package com.cn.bookmarktomb.excepotion;

import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;

public class AdminSetBeforeException extends RuntimeException {

	private  Integer code = ErrorCodeConstant.ADMIN_SET_BEFORE_CODE;

	public AdminSetBeforeException() {
		super(ErrorCodeConstant.ADMIN_SET_BEFORE_MSG);
	}

}
