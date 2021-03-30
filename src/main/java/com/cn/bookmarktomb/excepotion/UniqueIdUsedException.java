package com.cn.bookmarktomb.excepotion;

import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import lombok.Getter;

/**
 * The exception of Unique id has been used;
 * @author fallen-anglel
 */
@Getter
public class UniqueIdUsedException extends RuntimeException {

	private Integer code = ErrorCodeConstant.USER_UNIQUE_ID_USED_ERROR_CODE;

	public UniqueIdUsedException(String uidName, String uid) {
		super(generateMessage(uidName, uid));
	}

	public UniqueIdUsedException(int code, String uidName, String uid) {
		super(generateMessage(uidName, uid));
		this.code = code;
	}

	private static String generateMessage(String uidName, String uid) {
		return uidName + ":" + uid + ", has been used, please try another or contact with administrator;";
	}
}
