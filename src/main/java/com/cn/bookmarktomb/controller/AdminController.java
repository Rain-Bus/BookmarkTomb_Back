package com.cn.bookmarktomb.controller;

import cn.hutool.core.map.MapBuilder;
import com.cn.bookmarktomb.excepotion.SystemException;
import com.cn.bookmarktomb.model.cache.ConfigCache;
import com.cn.bookmarktomb.model.cache.UserInfoCache;
import com.cn.bookmarktomb.model.convert.OnlineInfoConvert;
import com.cn.bookmarktomb.model.convert.SystemInfoConvert;
import com.cn.bookmarktomb.model.convert.UserInfoConverter;
import com.cn.bookmarktomb.model.dto.OnlineInfoDTO.*;
import com.cn.bookmarktomb.model.dto.SystemInfoDTO.*;
import com.cn.bookmarktomb.model.dto.UserInfoDTO.*;
import com.cn.bookmarktomb.model.entity.Database;
import com.cn.bookmarktomb.model.entity.Email;
import com.cn.bookmarktomb.model.vo.AdminVO.*;
import com.cn.bookmarktomb.model.vo.SystemInfoVO.*;
import com.cn.bookmarktomb.service.OnlineService;
import com.cn.bookmarktomb.service.SystemInfoService;
import com.cn.bookmarktomb.service.UserInfoService;
import com.cn.bookmarktomb.util.MongoUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author fallen-angle
 * This is the controllers of admin;
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN')")
public class AdminController {
	private final OnlineService onlineService;
	private final UserInfoService userInfoService;
	private final OnlineInfoConvert onlineInfoConvert;
	private final UserInfoConverter userInfoConverter;
	private final SystemInfoService systemInfoService;
	private final SystemInfoConvert systemInfoConvert;

	private static final String USERNAME_FILED_NAME = "UName";
	private static final String USER_EMAIL_FIELD_NAME = "UEml";

	@PostMapping("/user")
	@ApiOperation("Add a new user")
	public ResponseEntity<Object> addUser(@Valid @RequestBody AdminCreateUserVO createUserVO) {
		RegisterDTO registerDTO = userInfoConverter.adminCreateVO2DTO(createUserVO);
		MongoUtil.detectUserUniqueIdIsUsed(userInfoService, USERNAME_FILED_NAME, registerDTO.getUsername());
		MongoUtil.detectUserUniqueIdIsUsed(userInfoService, USER_EMAIL_FIELD_NAME, registerDTO.getEmail());
		userInfoService.insertUser(registerDTO);
		UserBasicInfoDTO userBasicInfoDTO = userInfoService.selectByUserName(createUserVO.getUsername());
		return ResponseEntity.ok(userInfoConverter.userBasicInfoDTO2VO(userBasicInfoDTO));
	}

	@PutMapping("/user")
	@ApiOperation("Reset user's info")
	public ResponseEntity<Object> setUserInfo(@Valid @RequestBody AdminResetOtherInfoVO adminResetOtherInfoVO){
		ResetOtherInfoDTO resetOtherInfoDTO = userInfoConverter.adminResetOtherInfoVO2DTO(adminResetOtherInfoVO);
		userInfoService.updateOtherInfo(resetOtherInfoDTO);
		UserBasicInfoDTO userBasicInfoDTO = userInfoService.selectById(adminResetOtherInfoVO.getId());
		return ResponseEntity.ok(userInfoConverter.userBasicInfoDTO2VO(userBasicInfoDTO));
	}

	@PutMapping("/user/password")
	@ApiOperation("Reset user's password")
	public ResponseEntity<Object> setPassword(@Valid @RequestBody AdminResetPasswordVO adminSetPasswordVO) {
		ResetPasswordDTO resetPasswordDTO = userInfoConverter.adminResetPasswordVO2DTO(adminSetPasswordVO);
		userInfoService.updatePassword(resetPasswordDTO);
		onlineService.deleteById(adminSetPasswordVO.getId());
		UserInfoCache.removeUserFromCacheByUserId(adminSetPasswordVO.getId());
		return ResponseEntity.ok().build();
	}

	@PutMapping("/user/restore/{userId}")
	@ApiOperation("Restore a logoff user")
	public ResponseEntity<Object> restoreUser(@PathVariable Long userId) {
		userInfoService.restoreById(userId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/user/{userId}")
	@ApiOperation("Logoff the not admin user")
	public ResponseEntity<Object> logoffUser(@PathVariable Long userId) {
		userInfoService.deleteById(userId);
		onlineService.deleteById(userId);
		UserInfoCache.removeUserFromCacheByUserId(userId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/users")
	@ApiOperation("Get all users")
	public ResponseEntity<Object> getUsers() {
		List<UserBasicInfoDTO> userBasicInfoDTOs = userInfoService.listUsers();
		return ResponseEntity.ok(userInfoConverter.userBasicInfoDTO2VOs(userBasicInfoDTOs));
	}

	@GetMapping("/online")
	@ApiOperation("Get online info")
	public ResponseEntity<Object> getOnline() {
		List<GetOnlineInfoDTO> getOnlineInfoDTOs = onlineService.listInfos();
		return ResponseEntity.ok(onlineInfoConvert.getOnlineInfoDTO2VOs(getOnlineInfoDTOs));
	}

	@DeleteMapping("/offline/token/{token}")
	@ApiOperation("Offline a token")
	public ResponseEntity<Object> offlineUserByToken(@PathVariable("token") String token) {
		onlineService.deleteByToken(token);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/offline/user/{id}")
	@ApiOperation("Offline a user")
	public ResponseEntity<Object> offlineUser(@PathVariable("id") Long userId) {
		onlineService.deleteById(userId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/offline/all")
	@ApiOperation("Offline all user")
	public ResponseEntity<Object> offlineAllUser() {
		onlineService.clearUser();
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/cache")
	@ApiOperation("Clear all the user info cache")
	public ResponseEntity<Object> cacheClear() {
		UserInfoCache.clearUserCache();
		return ResponseEntity.ok().build();
	}

	@GetMapping("/system")
	@ApiOperation("Get system status")
	public ResponseEntity<Object> systemInfo() {
		GetSystemInfoDTO getSystemInfoDTO = systemInfoService.selectInfo();
		return ResponseEntity.ok(systemInfoConvert.getSystemInfoDTO2VO(getSystemInfoDTO));
	}

	@GetMapping("/system/email")
	@ApiOperation("Get email configuration")
	public ResponseEntity<Object> getEmailConfig() {
		boolean emailEnable = Objects.nonNull(ConfigCache.get(ConfigCache.EMAIL_ENABLE))
				&& (boolean) ConfigCache.get(ConfigCache.EMAIL_ENABLE);
		EmailVO emailVO = systemInfoConvert.emailDO2VO((Email) ConfigCache.get(ConfigCache.EMAIL));
		Map<String, Object> mailMap = MapBuilder.<String, Object>create()
				.put(ConfigCache.EMAIL_ENABLE, emailEnable)
				.put(ConfigCache.EMAIL, emailVO)
				.map();
		return ResponseEntity.ok(mailMap);
	}

	@PutMapping("/system/email")
	@ApiOperation("Change email configuration")
	public ResponseEntity<Object> updateEmailConfig(@Valid @RequestBody EmailVO updateEmailVO) {
		Email email = systemInfoConvert.emailVO2DO(updateEmailVO);
		Map<String, Object> configMap = MapBuilder.<String, Object>create()
				.put(ConfigCache.EMAIL_ENABLE, true)
				.put(ConfigCache.EMAIL, email)
				.map();
		ConfigCache.updateConfigs(configMap);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/system/email")
	@ApiOperation("Switch the email")
	public ResponseEntity<Object> disableEmailConfig() {
		Map<String, Object> configMap = MapBuilder.<String, Object>create()
				.put(ConfigCache.EMAIL_ENABLE, false)
				.put(ConfigCache.REGISTER_ENABLE, false)
				.map();
		ConfigCache.updateConfigs(configMap);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/system/db")
	@ApiOperation("Get database configuration")
	public ResponseEntity<Object> getDatabaseConfig() {
		return ResponseEntity.ok(systemInfoConvert.databaseDO2VO((Database) ConfigCache.get(ConfigCache.DATABASE)));
	}

	@PutMapping("/system/db")
	@ApiOperation("Change email configuration")
	public ResponseEntity<Object> updateDatabaseConfig(@Valid @RequestBody DatabaseVO databaseVO) {
		Database oldConfig = (Database) ConfigCache.get(ConfigCache.DATABASE);
		Database newConfig = systemInfoConvert.databaseVO2DO(databaseVO);
		newConfig.setPort(oldConfig.getPort());
		newConfig.setHost(oldConfig.getHost());
		newConfig.setDbname(oldConfig.getDbname());
		ConfigCache.updateConfig(ConfigCache.DATABASE, newConfig);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/system/register")
	@ApiOperation("Get register status")
	public ResponseEntity<Object> getRegisterConfig() {
		ConfigCache.initCache();
		Map<String, Object> registerMap = Map.of(ConfigCache.REGISTER_ENABLE, ConfigCache.get(ConfigCache.REGISTER_ENABLE));
		return ResponseEntity.ok(registerMap);
	}

	@PutMapping("/system/register")
	@ApiOperation("Enable register")
	public ResponseEntity<Object> enableRegisterConfig() {
		boolean emailEnable = Objects.nonNull(ConfigCache.get(ConfigCache.EMAIL_ENABLE))
				&& (boolean) ConfigCache.get(ConfigCache.EMAIL_ENABLE);
		if (!emailEnable) {
			throw new SystemException("Email can't use, can't register!");
		}
		ConfigCache.updateConfig(ConfigCache.REGISTER_ENABLE, true);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/system/register")
	@ApiOperation("Disable register")
	public ResponseEntity<Object> disableRegisterConfig() {
		ConfigCache.updateConfig(ConfigCache.REGISTER_ENABLE, false);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/system/port")
	@ApiOperation("Get system server port")
	public ResponseEntity<Object> getPortConfig() {
		return ResponseEntity.ok(Map.of(ConfigCache.SERVER_PORT, ConfigCache.get(ConfigCache.SERVER_PORT)));
	}

	@PutMapping("/system/port/{port}")
	@ApiOperation("Change system server port")
	public ResponseEntity<Object> updatePortConfig(@PathVariable("port") @Min(value = 0) @Max(value = 65535) Integer port) {
		ConfigCache.updateConfig(ConfigCache.SERVER_PORT, port);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/shutdown")
	@ApiOperation("Shutdown system")
	public void shutdownSystem() {
		System.exit(0);
	}

}
