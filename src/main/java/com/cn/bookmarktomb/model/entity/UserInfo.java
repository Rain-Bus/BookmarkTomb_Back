package com.cn.bookmarktomb.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * @author fallen-angle
 */
@Data
@NoArgsConstructor
@Document("user_info")
public class UserInfo {

	@Id
	private Long id;

	@Field("UName")
	private String username;

	@Field("UNick")
	private String nickname;

	@Field("UEml")
	private String email;

	@Field("UPwd")
	private String password;

	@Field("URole")
	private Boolean isAdmin;

	@Field("URmTm")
	private LocalDateTime removeTime;

	@Field("URTm")
	private LocalDateTime registerTime;

}
