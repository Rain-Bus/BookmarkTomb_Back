package com.cn.bookmarktomb.controller;

import cn.hutool.core.map.MapBuilder;
import com.cn.bookmarktomb.model.bean.ProjectProperties;
import com.cn.bookmarktomb.model.cache.ConfigCache;
import com.cn.bookmarktomb.model.convert.SystemInfoConvert;
import com.cn.bookmarktomb.model.convert.UserInfoConverter;
import com.cn.bookmarktomb.model.dto.UserInfoDTO.*;
import com.cn.bookmarktomb.model.vo.AdminVO.AdminCreateUserVO;
import com.cn.bookmarktomb.model.vo.SystemInfoVO.*;
import com.cn.bookmarktomb.service.UserInfoService;
import com.cn.bookmarktomb.util.MailUtil;
import com.cn.bookmarktomb.util.MongoUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class PublicInfoController {

	private final MailUtil mailUtil;
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

	@GetMapping("/test")
	@ApiOperation("Test api")
	public ResponseEntity<Object> test() {
		return ResponseEntity.ok("This is test API;");
	}

}
