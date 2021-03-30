package com.cn.bookmarktomb.excepotion;

import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import lombok.Getter;

/**
 * The exception of user visit unexpect url(mostly in rest)
 * @author fallen-angle
 */
@Getter
public class UnexpectUrlException extends RuntimeException {

	private Integer code = ErrorCodeConstant.UNEXPECT_URL_CODE;

	public UnexpectUrlException(int code) {
		super(ErrorCodeConstant.UNEXPECT_URL_MSG);
		this.code = code;
	}

	public UnexpectUrlException() {
		super(ErrorCodeConstant.UNEXPECT_URL_MSG);
	}
}
