package com.cn.bookmarktomb.model.convert;

import com.cn.bookmarktomb.model.entity.UserInfo;
import com.cn.bookmarktomb.model.vo.AdminVO.*;
import com.cn.bookmarktomb.model.vo.UserInfoVO.*;
import com.cn.bookmarktomb.model.dto.UserInfoDTO.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fallen-angle
 */
@Component
@Mapper(componentModel = "spring")
public interface UserInfoConverter {

	@Mapping(target = "isAdmin", expression = "java(false)")
	@Mapping(target = "registerTime", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "password", expression = "java(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(registerVO.getPassword()))")
	RegisterDTO registerVO2DTO(RegisterVO registerVO);

	@Mapping(target = "isAdmin", expression = "java(false)")
	@Mapping(target = "registerTime", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "password", expression = "java(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(createUserVO.getPassword()))")
	RegisterDTO adminCreateVO2DTO(AdminCreateUserVO createUserVO);

	ResetEmailDTO resetEmailVO2DTO(ResetEmailVO resetEmailVO);

	@Mapping(target = "isEnabled", expression = "java(!java.util.Optional.ofNullable(userInfo.getRemoveTime()).isPresent())")
	UserBasicInfoDTO userBasicInfoDO2DTO(UserInfo userInfo);

	List<UserBasicInfoDTO> userBasicInfoDO2DTOs(List<UserInfo> userInfos);

	@Mapping(target = "password", expression = "java(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(resetPasswordVO.getPassword()))")
	ResetPasswordDTO resetPasswordVO2DTO(ResetPasswordVO resetPasswordVO);

	@Mapping(target = "password", expression = "java(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(adminResetPasswordVO.getPassword()))")
	ResetPasswordDTO adminResetPasswordVO2DTO(AdminResetPasswordVO adminResetPasswordVO);

	@Mapping(target = "password", expression = "java(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(forgetPasswordVO.getPassword()))")
	ForgetPasswordDTO forgetPasswordVO2DTO(ForgetPasswordVO forgetPasswordVO);

	UserBasicInfoVO userBasicInfoDTO2VO(UserBasicInfoDTO userBasicInfoDTO);

	List<UserBasicInfoVO> userBasicInfoDTO2VOs(List<UserBasicInfoDTO> userBasicInfoDTOs);

	ResetOtherInfoDTO resetOtherInfoVO2DTO(ResetOtherInfoVO resetOtherInfoVO);

	ResetOtherInfoDTO adminResetOtherInfoVO2DTO(AdminResetOtherInfoVO adminResetOtherInfoVO);

	@Mapping(target = "id", expression = "java(java.time.LocalDateTime.now().getLong(java.time.temporal.ChronoField.SECOND_OF_DAY))")
	UserInfo registerDTO2DO(RegisterDTO registerDTO);

	UserInfo resetEmailDTO2DO(ResetEmailDTO resetEmailDTO);

	UserInfo resetPasswordDTO2DO(ResetPasswordDTO resetPasswordDTO);

	UserInfo resetOtherInfoDTO2DO(ResetOtherInfoDTO resetOtherInfoDTO);

}