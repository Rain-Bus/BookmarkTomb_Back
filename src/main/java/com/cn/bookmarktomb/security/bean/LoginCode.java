package com.cn.bookmarktomb.security.bean;

import lombok.Data;

/**
 * @author fallen-angle
 * This is the configuration of image code.
 */
@Data
public class LoginCode {

	private LoginCodeEnum codeType;

	private Long codeExpiration = 2L;

	private Integer codeLength = 2;

	private Integer codeImageWidth = 111;

	private Integer codeImageHeight = 36;

	private String codeFontName;

	private Integer codeFontSize = 25;

	public LoginCodeEnum getCodeType() {
		return codeType;
	}
}
