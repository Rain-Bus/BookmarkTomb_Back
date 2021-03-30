package com.cn.bookmarktomb.security.service;

import com.cn.bookmarktomb.excepotion.AccountUnabeledException;
import com.cn.bookmarktomb.security.bean.JwtLoginProperties;
import com.cn.bookmarktomb.security.token.JwtUser;
import com.cn.bookmarktomb.model.cache.UserInfoCache;
import com.cn.bookmarktomb.model.convert.UserInfoConverter;
import com.cn.bookmarktomb.model.dto.UserInfoDTO.UserBasicInfoDTO;
import com.cn.bookmarktomb.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

/**
 * @author This is the authentication class;
 */
@RequiredArgsConstructor
@Service("userDetailService")
public class UserDetailServiceImpl implements UserDetailsService {

	private final UserInfoService userInfoService;
	private final JwtLoginProperties properties;
	private final UserInfoConverter userInfoConverter;

	@Override
	public JwtUser loadUserByUsername(String account) {
		JwtUser userDetail;
		ArrayList<GrantedAuthority> authorities = new ArrayList<>();

		// If the user info is in cache, don't need to query mysql.
		if (properties.isCacheEnable() && UserInfoCache.getUserCache().containsKey(account)) {
			userDetail = UserInfoCache.getUserCache().get(account);
		} else {
			UserBasicInfoDTO userBasicInfo = userInfoService.selectByUserName(account);

			// Judge account is present in DB, and is enabled or not
			if (Objects.isNull(userBasicInfo)) {
				throw new UsernameNotFoundException("");
			} else if (Boolean.FALSE.equals(userBasicInfo.getIsEnabled())) {
				throw new AccountUnabeledException();
			} else {
				authorities.add(new SimpleGrantedAuthority
						(Optional.ofNullable(userBasicInfo.getIsAdmin()).orElse(false) ? "ROLE_ADMIN" : "ROLE_USER"));
				userDetail = new JwtUser(userInfoConverter.userBasicInfoDTO2VO(userBasicInfo), authorities);
				UserInfoCache.addUserToCache(account, userDetail);
			}
		}
		return userDetail;
	}

}