package com.cn.bookmarktomb.util;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.system.SystemUtil;
import com.cn.bookmarktomb.excepotion.SystemException;
import com.cn.bookmarktomb.model.constant.ApiConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author fallen-angle
 */
@Slf4j
public class CommonUtil {

	private static final String UNKNOWN = "unknown";
	private static final String X_FORWARDED_FOR = "x-forwarded-for";
	private static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
	private static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
	private static final Double BYTE_CONVERT_UNIT = 1024D;
	private static final String[] BYTE_UNITS = new String[] {"B", "KB", "MB", "GB"};

	public static String getIp(HttpServletRequest request) {
		String ip;
		String comma = ",";
		String localhost = "127.0.0.1";

		// Get the IP from request.
		if (detectIpIsBlankOrUnknown(request.getHeader(X_FORWARDED_FOR))) {
			ip = request.getHeader(X_FORWARDED_FOR);
		} else if (detectIpIsBlankOrUnknown(request.getHeader(PROXY_CLIENT_IP))) {
			ip = request.getHeader(PROXY_CLIENT_IP);
		} else if (detectIpIsBlankOrUnknown(request.getHeader(WL_PROXY_CLIENT_IP))) {
			ip = request.getHeader(WL_PROXY_CLIENT_IP);
		} else {
			ip = request.getRemoteAddr();
		}

		// Detect the IP is several or not.
		if (ip.contains(comma)) {
			ip = ip.split(comma)[0];
		}
		if (localhost.equals(ip)) {
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				log.error(e.getMessage(), e);
			}
		}
		return ip;
	}

	public static String getHostPublicIp() {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = null;
		String api = String.format(ApiConstant.IP_URL, "");
		try {
			rootNode = mapper.readTree(HttpUtil.get(api));
		} catch (JsonProcessingException e) {
			return SystemUtil.getHostInfo().getAddress();
		}
		return rootNode.get("ip").asText();

	}

	public static String getBrowser(HttpServletRequest request) {
		UserAgent userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"));
		if (userAgent.isMobile()) {
			return userAgent.getOs().getName();
		}
		return userAgent.getBrowser().getName();
	}

	public static String getAddress(String ip){
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = null;
		String api = String.format(ApiConstant.IP_URL, ip);
		try {
			rootNode = mapper.readTree(HttpUtil.get(api));
		} catch (JsonProcessingException e) {
			throw new SystemException("Can't resolve the ip address's location: " + ip);
		}
		return rootNode.get("addr").asText();
	}

	public static String getReadableFileSize(long size) {
		double sizeDouble = (double)size;
		for (String byteUnit : BYTE_UNITS) {
			if (sizeDouble < BYTE_CONVERT_UNIT) {
				return String.format("%.2f", sizeDouble) + byteUnit;
			}
			sizeDouble /= BYTE_CONVERT_UNIT;
		}
		return String.format("%.2f", sizeDouble) + BYTE_UNITS[BYTE_UNITS.length-1];
	}

	public static String getEmailVerifyCode() {
		return IdUtil.simpleUUID().substring(0, 6);
	}

	private static boolean detectIpIsBlankOrUnknown(String ip) {
		return !StrUtil.isBlank(ip) && !UNKNOWN.equalsIgnoreCase(ip);
	}

	private CommonUtil(){}

}
