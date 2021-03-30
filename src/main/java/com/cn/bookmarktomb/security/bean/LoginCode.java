package com.cn.bookmarktomb.security.bean;

import lombok.Data;

/**
 * This is storage the login code in configuration;
 * @author fallen-angle
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
