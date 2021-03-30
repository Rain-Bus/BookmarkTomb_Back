package com.cn.bookmarktomb.service;

import com.cn.bookmarktomb.model.dto.UserInfoDTO.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fallen-angle
 */
@Service
public interface UserInfoService {

	void insertUser(RegisterDTO registerDTO);

	void deleteById(Long userId);

	void restoreById(Long userId);

	void updateEmail(ResetEmailDTO resetEmailDTO);

	void updateOtherInfo(ResetOtherInfoDTO resetOtherInfoDTO);

	void updatePassword(ResetPasswordDTO resetPasswordDTO);

	void updateForgetPassword(ForgetPasswordDTO forgetPasswordDTO);

	List<UserBasicInfoDTO> listUsers();

	/**
	 * to-do: The fuzzy search hasn't completed yet.
	 */
	List<UserBasicInfoDTO> listByUserName(String userName);

	String selectPasswordById(Long id);

	UserBasicInfoDTO selectByUserName(String userName);

	UserBasicInfoDTO selectById(Long userId);

	long countSameField(String fieldName, Object fieldValue);
}
