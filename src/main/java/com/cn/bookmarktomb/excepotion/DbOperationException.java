package com.cn.bookmarktomb.excepotion;

import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import lombok.Getter;

/**
 * The exception of DB operation;
 * @author fallen-angle
 */
@Getter
public class DbOperationException extends RuntimeException {

	private Integer code = ErrorCodeConstant.DB_OPERATION_CODE;

	public DbOperationException(int code, String message) {
		super(message);
		this.code = code;
	}

	public DbOperationException(String message) {
		super(message);
	}

}
