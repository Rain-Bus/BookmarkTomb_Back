package com.cn.bookmarktomb.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.cn.bookmarktomb.excepotion.SystemException;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * @author fallen-angle
 */
public class JsonUtil {

	private JsonUtil(){}

	public static String mapToJson(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		String result = null;
		try {
			JavaTimeModule timeModule = new JavaTimeModule();
			timeModule.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
			mapper.registerModule(timeModule);
			result = mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new SystemException("Can't map '" + object +"' to Json!");
		}
		return result;
	}

	public static  <T> T mapToObject(String str, Class<T> tClass) {
		ObjectMapper mapper = new ObjectMapper();
		T result = null;
		try {
			JavaTimeModule timeModule = new JavaTimeModule();
			timeModule.addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
			mapper.registerModule(timeModule);
			result = mapper.readValue(str, tClass);
		} catch (JsonProcessingException e) {
			throw new SystemException("Can't map Json(" + str + ") to " + tClass.getName() + "!");
		}
		return result;
	}

	public static Object readTree(String json) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readTree(json);
		} catch (JsonProcessingException e) {
			throw new SystemException("Can't remove the slashes in " + json);
		}
	}

	public static JSONObject readJsonFile(String path) {
		File jsonFile = new File(path);
		if (!jsonFile.exists()) {
			throw new SystemException("Configure file dismissed!");
		} else if (!(jsonFile.canRead() && jsonFile.canWrite())) {
			throw new SystemException("Configure file can't read or write!");
		}
		JSONObject jsonObject;
		try {
			jsonObject = JSONUtil.readJSONObject(jsonFile, Charset.defaultCharset());
		} catch (JSONException e) {
			throw new SystemException("Configure file is error");
		}
		return jsonObject;
	}

	public static void writeJsonFile(JSONObject object, String path) {
		FileUtil.writeString(object.toStringPretty(), path, StandardCharsets.UTF_8);
	}

	public static void setSubObject(JSONObject object, String subName, Object subObject) {
		object.set(subName, subObject);
	}

}
