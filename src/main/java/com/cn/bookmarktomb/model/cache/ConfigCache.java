package com.cn.bookmarktomb.model.cache;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONObject;
import cn.hutool.system.SystemUtil;
import com.cn.bookmarktomb.excepotion.SystemException;
import com.cn.bookmarktomb.model.entity.Database;
import com.cn.bookmarktomb.model.entity.Email;
import com.cn.bookmarktomb.util.JsonUtil;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author fallen-angle
 * This is the cache of configuration.
 * Will load from conf file before boot, and will reload after change configuration or change conf file.
 */
public class ConfigCache {

	public static final String MD_5 = "md5";
	public static final String EMAIL = "email";
	public static final String JAR_PATH = "jar";
	public static final String CONF_PATH = "conf";
	public static final String INIT_FLAG = "init";
	public static final String ADMIN_FLAG = "admin";
	public static final String DATABASE = "database";
	public static final String STARTED_OK = "startedOk";
	public static final String SERVER_PORT = "serverPort";
	public static final String EMAIL_ENABLE = "emailEnable";
	public static final String STARTED_FLAG = "startedFlag";
	public static final String DATABASE_AUTH = "databaseAuh";
	public static final String DATABASE_REACH = "databaseReach";
	public static final String REGISTER_ENABLE = "registerEnable";

	static Map<String, Object> configMap = MapBuilder.<String, Object>create()
			.put(MD_5, null)
			.put(EMAIL, null)
			.put(DATABASE, null)
			.put(JAR_PATH, null)
			.put(INIT_FLAG, false)
			.put(STARTED_OK, false)
			.put(ADMIN_FLAG, false)
			.put(SERVER_PORT, null)
			.put(EMAIL_ENABLE, null)
			.put(DATABASE_AUTH, true)
			.put(STARTED_FLAG, false)
			.put(DATABASE_REACH, true)
			.put(REGISTER_ENABLE, null)
			.put(CONF_PATH, SystemUtil.getUserInfo().getHomeDir() + "/.bookmark_tomb/conf.json")
			.map();

	public static void initCache() {
		JSONObject jsonObject;

		// If the conf file is not correct, represent the system is not initialized.
		try {
			jsonObject = JsonUtil.readJsonFile(ConfigCache.get(ConfigCache.CONF_PATH).toString());
		} catch (SystemException e) {
			configMap.put(INIT_FLAG, false);
			return;
		}

		configMap.put(INIT_FLAG, true);
		configMap.put(SERVER_PORT, jsonObject.get(SERVER_PORT));
		configMap.put(MD_5, MD5.create().digestHex(jsonObject.toString(), Charset.defaultCharset()));
		configMap.put(DATABASE, jsonObject.get(DATABASE, Database.class));
		configMap.put(EMAIL, jsonObject.get(EMAIL, Email.class));
		configMap.put(EMAIL_ENABLE, jsonObject.get(EMAIL_ENABLE));
		configMap.put(REGISTER_ENABLE, (Boolean) jsonObject.get(EMAIL_ENABLE) && (Boolean) jsonObject.get(REGISTER_ENABLE));
		ApplicationHome h = new ApplicationHome(ConfigCache.class);
		File jarFile = h.getSource();
		configMap.put(JAR_PATH, jarFile.getParentFile().toString());
	}

	public static Object get(String configKey) {
		return configMap.get(configKey);
	}

	public static Map<String, Object> getAllConfig() {
		return configMap;
	}

	public static void updateConfig(String fieldName, Object object) {
		updateConfigs(Map.of(fieldName, object));
	}

	public static void updateConfigs(Map<String, Object> changeMap) {
		// Read configuration from conf file, and then write the changed configuration.
		JSONObject jsonObject = JsonUtil.readJsonFile(configMap.get(CONF_PATH).toString());
		for (Map.Entry<String, Object> config: changeMap.entrySet()) {
			JsonUtil.setSubObject(jsonObject, config.getKey(), config.getValue());
		}
		JsonUtil.writeJsonFile(jsonObject, configMap.get(CONF_PATH).toString());

		initCache();
	}

	public static void initConfig(Map<String, Object> changeMap) {
		// When init the conf file is blank, so need new a object.
		JSONObject jsonObject = new JSONObject();

		for (Map.Entry<String, Object> config: changeMap.entrySet()) {
			JsonUtil.setSubObject(jsonObject, config.getKey(), config.getValue());
		}
		JsonUtil.writeJsonFile(jsonObject, configMap.get(CONF_PATH).toString());

		initCache();
	}

	/**
	 * This only change the cache configuration, not change the conf file.
	 */
	public static void set(String cacheKey, Object cacheValue) {
		configMap.put(cacheKey, cacheValue);
	}

	private ConfigCache() {}

}
