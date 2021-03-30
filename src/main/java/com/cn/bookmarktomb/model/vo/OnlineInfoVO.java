package com.cn.bookmarktomb.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author fallen-angle
 */
public class OnlineInfoVO {

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class GetOnlineInfoVO {

		private String username;

		private Long id;

		private String device;

		private String ip;

		private String address;

		private String rememberMe;

	}

	private OnlineInfoVO(){}

	@Override
	public String toString() {
		return "";
	}

}
