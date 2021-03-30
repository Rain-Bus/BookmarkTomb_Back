package com.cn.bookmarktomb.model.vo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.cn.bookmarktomb.model.constant.RegularConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

public class UserInfoVO {


	@Getter
	@Setter
	@ToString
	@ApiModel
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class UserBasicInfoVO {

        private Long id;

		private String username;

		private String email;

		private String nickname;

		@JsonIgnore
		private String password;

		private Boolean isAdmin;

		private Boolean isEnabled;

    }

	@Getter
	@Setter
	@ToString
	@ApiModel
    @NoArgsConstructor
    public static class ResetEmailVO {

        @Null
        @ApiModelProperty(hidden = true)
        private Long id;

        @NotNull(message = "This Filed can't be null!")
        @Pattern(regexp = RegularConstant.EMAIL_REGEXP, message = "Input correct email address! Email address length must between 9 and 40!")
        private String email;

		@NotNull
		private String code;

        @NotNull
		@Pattern(regexp = RegularConstant.PASSWORD_REGEXP, message = "Password must be composed by A-Z, a-z, 0-9, _ and @! The length must between 8 and 20!")
		private String password;

    }

	@Getter
	@Setter
	@ToString
	@ApiModel
    @NoArgsConstructor
    public static class RegisterVO {
        @NotNull(message = "This Filed can't be null!")
        @Pattern(regexp = RegularConstant.EMAIL_REGEXP, message = "Input correct email address! Email address length must between 9 and 40!")
        private String email;

        @NotNull(message = "This Filed can't be null!")
        @Pattern(regexp = RegularConstant.USERNAME_REGEXP, message = "Username must be composed by low and up characters, numbers and underline! And must begin with character! The length must between 6 and 20!")
        private String username;

        @NotNull(message = "This Filed can't be null!")
        @Pattern(regexp = RegularConstant.PASSWORD_REGEXP, message = "Password must be composed by A-Z, a-z, 0-9, _ and @! The length must between 8 and 20!")
        private String password;

        @NotNull
        private String code;
    }

	@Getter
	@Setter
	@ToString
	@ApiModel
    @NoArgsConstructor
    public static class ResetOtherInfoVO {

        @Null
        @ApiModelProperty(hidden = true)
        private Long id;

        @NotNull(message = "This Filed can't be null!")
        @Pattern(regexp = RegularConstant.USERNAME_REGEXP, message = "Username must be composed by low and up characters, numbers and underline! And must begin with character! The length must between 6 and 20!")
        private String nickname;
    }

	@Getter
	@Setter
	@ToString
	@ApiModel
    @NoArgsConstructor
    public static class ResetPasswordVO {

        @Null
        @ApiModelProperty(hidden = true)
        private Long id;

        @NotNull(message = "This Filed can't be null!")
        @Pattern(regexp = RegularConstant.PASSWORD_REGEXP, message = "Password must be composed by A-Z, a-z, 0-9, _ and @! The length must between 8 and 20!")
        private String password;

        @NotNull(message = "This Filed can't be null!")
        @Pattern(regexp = RegularConstant.PASSWORD_REGEXP, message = "Password must be composed by A-Z, a-z, 0-9, _ and @! The length must between 8 and 20!")
        private String oldPassword;
    }

	@Getter
	@Setter
	@ToString
	@ApiModel
    @NoArgsConstructor
    public static class LoginVO {

        @NotNull(message = "This Filed can't be null!")
//        @Pattern(regexp = RegularConstant.EMAIL_OR_TELEPHONE_REGEXP, message = "Input correct email address or telephone number!")
        private String account;

        @NotNull(message = "This Filed can't be null!")
        @Pattern(regexp = RegularConstant.PASSWORD_REGEXP, message = "The password format is incorrect")
        private String password;

        @NotNull
        private String code;

        @NotNull
        private String codeUid;

        @NotNull
		private Boolean rememberMe;
    }

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class ForgetPasswordVO {

		@NotNull
		@Email
		private String email;

		@NotNull
		@Email
		private String code;

		@NotNull(message = "This Filed can't be null!")
		@Pattern(regexp = RegularConstant.PASSWORD_REGEXP, message = "Password must be composed by A-Z, a-z, 0-9, _ and @! The length must between 8 and 20!")
		private String password;
	}

	private UserInfoVO(){}

    @Override
    public String toString() {
        return "";
    }

}
