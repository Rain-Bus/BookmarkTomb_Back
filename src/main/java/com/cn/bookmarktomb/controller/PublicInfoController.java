package com.cn.bookmarktomb.controller;

import cn.hutool.core.map.MapBuilder;
import com.cn.bookmarktomb.config.AdminConfig;
import com.cn.bookmarktomb.model.bean.ProjectProperties;
import com.cn.bookmarktomb.model.cache.ConfigCache;
import com.cn.bookmarktomb.model.convert.SystemInfoConvert;
import com.cn.bookmarktomb.model.convert.UserInfoConverter;
import com.cn.bookmarktomb.model.dto.UserInfoDTO.RegisterDTO;
import com.cn.bookmarktomb.model.dto.UserInfoDTO.UserBasicInfoDTO;
import com.cn.bookmarktomb.model.vo.AdminVO.AdminCreateUserVO;
import com.cn.bookmarktomb.model.vo.SystemInfoVO.EmailVO;
import com.cn.bookmarktomb.model.vo.SystemInfoVO.InitVO;
import com.cn.bookmarktomb.service.UserInfoService;
import com.cn.bookmarktomb.util.MailUtil;
import com.cn.bookmarktomb.util.MongoUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * @author fallen-angle
 * This is the controllers of public info;
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class PublicInfoController {

	private final MailUtil mailUtil;
	private final AdminConfig adminConfig;
	private final UserInfoService userInfoService;
	private final ProjectProperties projectProperties;
	private final SystemInfoConvert systemInfoConvert;
	private final UserInfoConverter userInfoConverter;

	private static final String USERNAME_FILED_NAME = "UName";
	private static final String USER_EMAIL_FIELD_NAME = "UEml";

	@GetMapping("/system")
	@ApiOperation("Get system version and name")
	public ResponseEntity<Object> getSystemInfo() {
		return ResponseEntity.ok(projectProperties);
	}

	@PostMapping("/init")
	@ApiOperation("Init the configuration")
	public ResponseEntity<Object> initSystem(@Valid @RequestBody InitVO initVO) {
		boolean initFlag = (boolean)ConfigCache.get(ConfigCache.INIT_FLAG);
		if (initFlag) {
			return ResponseEntity.ok("The system has been initialized before!");
		}
		Map<String, Object> initMap = MapBuilder.<String, Object>create()
				.put(ConfigCache.SERVER_PORT, initVO.getServerPort())
				.put(ConfigCache.REGISTER_ENABLE, initVO.getRegisterEnable())
				.put(ConfigCache.EMAIL_ENABLE, initVO.getEmailEnable())
				.put(ConfigCache.EMAIL, systemInfoConvert.emailVO2DO(initVO.getEmail()))
				.put(ConfigCache.DATABASE, systemInfoConvert.databaseVO2DO(initVO.getDatabase()))
				.map();
		ConfigCache.initConfig(initMap);
		System.exit(0);
		return ResponseEntity.ok("Please rerun start.sh to start the system!");
	}

	@PostMapping("/admin")
	@ApiOperation("Init admin account")
	public ResponseEntity<Object> createAdmin(@Valid @RequestBody AdminCreateUserVO createUserVO) {
		if ((boolean) ConfigCache.get(ConfigCache.ADMIN_FLAG)) {
			return ResponseEntity.ok("The admin has been set before!");
		}
		RegisterDTO registerDTO = userInfoConverter.adminCreateVO2DTO(createUserVO);
		registerDTO.setIsAdmin(true);
		MongoUtil.detectUserUniqueIdIsUsed(userInfoService, USERNAME_FILED_NAME, registerDTO.getUsername());
		MongoUtil.detectUserUniqueIdIsUsed(userInfoService, USER_EMAIL_FIELD_NAME, registerDTO.getEmail());
		userInfoService.insertUser(registerDTO);
		UserBasicInfoDTO userBasicInfoDTO = userInfoService.selectByUserName(createUserVO.getUsername());
		ConfigCache.set(ConfigCache.ADMIN_FLAG, true);
		return ResponseEntity.ok(userInfoConverter.userBasicInfoDTO2VO(userBasicInfoDTO));
	}

	@PostMapping("/mail")
	@ApiOperation("Test email server's usability")
	public ResponseEntity<Object> testEmail(@Valid @RequestBody EmailVO emailVO) {
		mailUtil.sendTestEmail(systemInfoConvert.emailVO2DO(emailVO));
		return ResponseEntity.ok().build();
	}

	@GetMapping("/enable")
	@ApiOperation("Get the status of register and email")
	public ResponseEntity<Object> getEnable() {
		Map<String, Boolean> enableMap = MapBuilder.<String, Boolean>create()
				.put(ConfigCache.ADMIN_FLAG, (Boolean) ConfigCache.get(ConfigCache.EMAIL_ENABLE))
				.put(ConfigCache.REGISTER_ENABLE, (Boolean) ConfigCache.get(ConfigCache.REGISTER_ENABLE))
				.map();
		return ResponseEntity.ok(enableMap);
	}

	@GetMapping("/db")
	@ApiOperation("Reconnect database")
	public ResponseEntity<Object> resetDb() {
		boolean authFlag = (boolean) ConfigCache.get(ConfigCache.DATABASE_AUTH);
		boolean reachFlag = (boolean) ConfigCache.get(ConfigCache.DATABASE_REACH);

		if (reachFlag && authFlag) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have been connected to db!");
		} else {
			try {
				adminConfig.execDetectAdmin();
				return ResponseEntity.ok("Connected");
			} catch (Exception ignored) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
			}
		}
	}

	@GetMapping("/test")
	@ApiOperation("Test api")
	public ResponseEntity<Object> test() {
		return ResponseEntity.ok("This is test API;");
	}

}
