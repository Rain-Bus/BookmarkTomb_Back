package com.cn.bookmarktomb.excepotion;

import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import lombok.Getter;

@Getter
public class AccountUnabeledException extends RuntimeException {

	private Integer code = ErrorCodeConstant.USER_ACCOUNT_NOT_ENABLED_CODE;

	public AccountUnabeledException() {
		super("Account is Unlabeled!");
	}

}
