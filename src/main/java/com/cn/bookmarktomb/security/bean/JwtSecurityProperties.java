package com.cn.bookmarktomb.security.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author fallen-angle
 * This is used to get the security property in spring configuratiuon.
 */
@Getter
@Setter
@NoArgsConstructor
public class JwtSecurityProperties {

	/**
	 * Request header: Authorization
	 */
	private String header;

	/**
	 * The prefix of token, when use it need add a space;
	 */
	private String tokenPrefix;

	/**
	 * Encoder with base64 more than 88 characters;
	 */
	private String base64Secret;

	private Integer tokenExpireSeconds;

	/**
	 * The online user's data key in the redis;
	 */
	private String onlineKey;

	/**
	 * The valid code key in the redis;
	 */
	private String codeKey;

	/**
	 * Detect the token need to renew or not; If the expire time is less than this, will renew the token;
	 */
	private Long renewTriggerTime;

	/**
	 * The renew seconds;
	 */
	private Long renewDuration;

	public String getTokenPrefix() {
		return tokenPrefix + " ";
	}

}
