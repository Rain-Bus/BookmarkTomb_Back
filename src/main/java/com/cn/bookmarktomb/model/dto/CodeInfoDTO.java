package com.cn.bookmarktomb.model.dto;

import lombok.*;

/**
 * @author fallen-angle
 */
public class CodeInfoDTO {

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class InsertCodeDTO {

		private String uid;

		private String result;

	}

	private CodeInfoDTO(){}

	@Override
	public String toString() {
		return "";
	}
}
