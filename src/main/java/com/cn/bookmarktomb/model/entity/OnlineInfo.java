package com.cn.bookmarktomb.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * @author fallen-angle
 */
@Data
@Document("online_info")
public class OnlineInfo {

	@Id
	private String token;

	@Field("UName")
	private String username;

	@Field("UId")
	private Long id;

	@Field("Dev")
	private String device;

	@Field("Ip")
	private String ip;

	@Field("Addr")
	private String address;

	@Field("Rem")
	private Boolean rememberMe;

	@Field("LTm")
	private LocalDateTime loginTime;

	@Field("ORTm")
	private LocalDateTime removeTime;
}
