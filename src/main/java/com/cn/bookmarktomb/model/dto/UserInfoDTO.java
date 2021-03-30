package com.cn.bookmarktomb.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


/**
 * @author fallen-anlgle
 */
public class UserInfoDTO {


    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class UserBasicInfoDTO {

        private Long id;

        private String username;

        private String email;

        private String nickname;

        private String password;

        private Boolean isAdmin;

        private Boolean isEnabled;

    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class ResetEmailDTO {

        private Long id;

        private String email;

        private String code;

        private String password;

    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class RegisterDTO {

        private Boolean isAdmin;

        private String username;

        private String email;

        private String password;

        private LocalDateTime registerTime;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class ResetOtherInfoDTO {

        private Long id;

        private String nickname;

    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class ResetPasswordDTO {

        private Long id;

        private String password;

        private String oldPassword;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class ForgetPasswordDTO {

        private Long email;

        private String password;

    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OnlineUserDTO implements Serializable {

        private String username;

        private Long id;

        private String browser;

        private String ip;

        private String address;

        private String token;

        private Date loginTime;

    }

    private UserInfoDTO(){}

    @Override
    public String toString() {
        return "";
    }

}