package com.cn.bookmarktomb.model.vo;

import com.cn.bookmarktomb.model.constant.RegularConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author fallen-angle
 */
public class AdminVO {

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class AdminResetPasswordVO {

		@NotNull
		private Long id;

		@NotNull(message = "This Filed can't be null!")
		@Pattern(regexp = RegularConstant.PASSWORD_REGEXP, message = "Password must be composed by A-Z, a-z, 0-9, _ and @! The length must between 8 and 20!")
		private String password;

	}

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class AdminResetOtherInfoVO {
		@NotNull
		private Long id;

		@Pattern(regexp = RegularConstant.USERNAME_REGEXP, message = "Username must be composed by low and up characters, numbers and underline! And must begin with character! The length must between 6 and 20!")
		private String nickname;
	}

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class AdminCreateUserVO {
		@NotNull
		@Pattern(regexp = RegularConstant.USERNAME_REGEXP)
		String username;

		@NotNull
		@Pattern(regexp = RegularConstant.PASSWORD_REGEXP)
		String password;

		@Email
		@NotNull
		String email;

	}

	private AdminVO(){}

	@Override
	public String toString() {
		return "";
	}
}
