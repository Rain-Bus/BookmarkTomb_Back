package com.cn.bookmarktomb.service.impl;

import cn.hutool.core.map.MapBuilder;
import com.cn.bookmarktomb.excepotion.EntityNotFoundException;
import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import com.cn.bookmarktomb.model.convert.UserInfoConverter;
import com.cn.bookmarktomb.model.dto.UserInfoDTO.*;
import com.cn.bookmarktomb.model.entity.UserInfo;
import com.cn.bookmarktomb.service.UserInfoService;
import com.cn.bookmarktomb.util.MongoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author fallen-angle
 */
@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

	private final MongoTemplate mongoTemplate;
	private final UserInfoConverter userInfoConverter;

	private final List<Boolean> notAdminRole = Arrays.asList(false, null);

	/*-------------------------------------------< Service >----------------------------------------------*/

	@Override
	public void insertUser(RegisterDTO registerDTO) {
		UserInfo userInfo = userInfoConverter.registerDTO2DO(registerDTO);
		mongoTemplate.insert(userInfo);
	}

	@Override
	public void deleteById(Long userId) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("_id", userId)
				.put("URmTm", null)
		// This field limit user can't logoff admin user
				.put("URole", notAdminRole)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		Update update = getDeleteUpdate(null);
		if (mongoTemplate.updateFirst(query, update, UserInfo.class).getMatchedCount() == 0) {
			throw new EntityNotFoundException("User not found when delete " + userId);
		}
	}

	@Override
	public void restoreById(Long userId) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("_id", userId)
				.put("URmTm", null)
				.put("URole", notAdminRole)
				.map();
		Query query = MongoUtil.getEqQueryByMapAndExistFields(queryMap, "URmTm");
		Update update = getRestoreUpdate();
		if (mongoTemplate.updateFirst(query, update, UserInfo.class).getMatchedCount() == 0) {
			throw new EntityNotFoundException("User not found when delete " + userId);
		}
	}

	@Override
	public void updateEmail(ResetEmailDTO resetEmailDTO) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("URmTm", null)
				.put("_id", resetEmailDTO.getId())
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		Update update = getEmailUpdate(resetEmailDTO);
		if (mongoTemplate.updateFirst(query, update, UserInfo.class).getMatchedCount() == 0) {
			throw new EntityNotFoundException("User not found when update " + resetEmailDTO.getId());
		}
	}

	@Override
	public void updateOtherInfo(ResetOtherInfoDTO resetOtherInfoDTO) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("URmTm", null)
				.put("_id", resetOtherInfoDTO.getId())
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		Update update = getOtherInfoUpdate(resetOtherInfoDTO);
		if (mongoTemplate.updateFirst(query, update, UserInfo.class).getMatchedCount() == 0) {
			throw new EntityNotFoundException("User not found when update " + resetOtherInfoDTO.getId());
		}
	}

	@Override
	public void updatePassword(ResetPasswordDTO resetPasswordDTO) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("URmTm", null)
				.put("_id", resetPasswordDTO.getId())
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		Update update = getPasswordUpdate(resetPasswordDTO.getPassword());
		if (mongoTemplate.updateFirst(query, update, UserInfo.class).getMatchedCount() == 0) {
			throw new EntityNotFoundException(ErrorCodeConstant.USER_ACCOUNT_NOT_FOUNT_CODE ,"User not found when update " + resetPasswordDTO.getId());
		}
	}

	@Override
	public void updateForgetPassword(ForgetPasswordDTO forgetPasswordDTO) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("URmTm", null)
				.put("UEml", forgetPasswordDTO.getEmail())
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		Update update = getPasswordUpdate(forgetPasswordDTO.getPassword());
		if (mongoTemplate.updateFirst(query, update, UserInfo.class).getMatchedCount() == 0) {
			throw new EntityNotFoundException(ErrorCodeConstant.USER_ACCOUNT_NOT_FOUNT_CODE, "User not found when update " + forgetPasswordDTO.getPassword());
		}
	}

	@Override
	public List<UserBasicInfoDTO> listByUserName(String userName) {
		Query query = MongoUtil.getLikeQueryByParam("UName", userName);
		List<UserInfo> userInfos = mongoTemplate.find(query, UserInfo.class);
		return userInfoConverter.userBasicInfoDO2DTOs(userInfos);
	}

	@Override
	public List<UserBasicInfoDTO> listUsers() {
		List<UserInfo> userInfos = mongoTemplate.findAll(UserInfo.class);
		return userInfoConverter.userBasicInfoDO2DTOs(userInfos);
	}

	@Override
	public String selectPasswordById(Long id) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("_id", id)
				.map();
		List<String> filedLists = new ArrayList<>(1);
		filedLists.add("UPwd");
		Query query = MongoUtil.getEqAndFieldQuery(queryMap, filedLists);
		return Optional.ofNullable(mongoTemplate.findOne(query, UserInfo.class))
				.map(UserInfo::getPassword)
				.orElse("");
	}

	@Override
	public UserBasicInfoDTO selectByUserName(String userName) {
		Query query = MongoUtil.getEqQueryByParam("UName", userName);
		UserInfo userInfo = mongoTemplate.findOne(query, UserInfo.class);
		return userInfoConverter.userBasicInfoDO2DTO(userInfo);
	}

	@Override
	public UserBasicInfoDTO selectById(Long userId) {
		Query query = MongoUtil.getEqQueryByParam("_id", userId);
		UserInfo userInfo = mongoTemplate.findOne(query, UserInfo.class);
		return userInfoConverter.userBasicInfoDO2DTO(userInfo);
	}

	@Override
	public long countSameField(String fieldName, Object fieldValue) {
		Query query = MongoUtil.getEqQueryByParam(fieldName, fieldValue);
		return mongoTemplate.count(query, UserInfo.class);
	}

	/*----------------------------------------< Generate Update >--------------------------------------*/

	private Update getEmailUpdate(ResetEmailDTO resetEmailDTO) {
		return new Update()
				.set("UEml", resetEmailDTO.getEmail());
	}

	private Update getDeleteUpdate(LocalDateTime removeTime) {
		return new Update()
				.set("URmTm", Objects.isNull(removeTime) ? LocalDateTime.now().plusDays(15L) : removeTime);
	}

	private Update getRestoreUpdate() {
		return new Update()
				.unset("URmTm");
	}

	private Update getOtherInfoUpdate(ResetOtherInfoDTO resetOtherInfoDTO) {
		return new Update()
				.set("UNick", resetOtherInfoDTO.getNickname());
	}

	private Update getPasswordUpdate(String password) {
		return new Update()
				.set("UPwd", password);
	}
}
