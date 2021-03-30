package com.cn.bookmarktomb.model.cache;

import com.cn.bookmarktomb.security.token.JwtUser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fallen-angle
 * This is the cache of user infos, to reduce the visit of database.
 * But this feature is very useless, and make code more complex.
 */
public class UserInfoCache {

	static Map<String, JwtUser> userCache = new ConcurrentHashMap<>();

	public static Map<String, JwtUser> getUserCache() {
		return userCache;
	}

	public static void addUserToCache(String userName, JwtUser jwtUser) {
		userCache.put(userName, jwtUser);
	}

	public static void removeUserFromCacheByUserId(Long userId) {
		userCache.forEach((k,v) -> {
			if (v.getUserBasicInfoVO().getId().equals(userId)) {
				userCache.remove(k);
			}
		});
	}

	public static void clearUserCache() {
		userCache.clear();
	}

	private UserInfoCache(){}

}
