package com.cn.bookmarktomb.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class OnlineInfoDTO {

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class InsertOnlineInfoDTO {

		private String username;

		private Long id;

		private String device;

		private String ip;

		private String address;

		private String token;

		private Boolean rememberMe;
	}

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class GetOnlineInfoDTO {

		private String token;

		private String username;

		private Long id;

		private String device;

		private String ip;

		private String address;

		private Boolean rememberMe;

	}

	private OnlineInfoDTO(){}

	@Override
	public String toString() {
		return "";
	}

}
