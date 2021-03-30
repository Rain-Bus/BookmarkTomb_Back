package com.cn.bookmarktomb.security.token;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.cn.bookmarktomb.security.bean.JwtSecurityProperties;
import com.cn.bookmarktomb.model.constant.CommonConstant;
import com.cn.bookmarktomb.model.entity.OnlineInfo;
import com.cn.bookmarktomb.model.vo.UserInfoVO.*;
import com.cn.bookmarktomb.service.OnlineService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is used to generate and parse token;
 * @author fallen-angle
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider implements InitializingBean {

	private final OnlineService onlineService;
	private final JwtSecurityProperties properties;

	private JwtParser jwtParser;
	private JwtBuilder jwtBuilder;
	private static final String AUTHORITIES_KEY = "auth";

	@Override
	public void afterPropertiesSet() {
		byte[] keyBytes = Decoders.BASE64.decode(properties.getBase64Secret());
		Key key = Keys.hmacShaKeyFor(keyBytes);
		jwtParser = Jwts.parserBuilder()
				.setSigningKey(key)
				.build();
		jwtBuilder = Jwts.builder()
				.signWith(key, SignatureAlgorithm.HS512);
	}

	/**
	 * The token doesn't need to set expire;
	 * @return The token has been generated;
	 */
	public String generateToken(UserBasicInfoVO userBasicInfoVO) {

		Map<String, Object> userInfo = new HashMap<>(2);
		userInfo.put("username", userBasicInfoVO.getUsername());
		userInfo.put("isAdmin", Optional.ofNullable(userBasicInfoVO.getIsAdmin()).orElse(false));

		return jwtBuilder
				.setClaims(userInfo)
				.setSubject(userBasicInfoVO.getId().toString())
				.setId(IdUtil.randomUUID())
				.compact();
	}

	/**
	 * Analyze and parse the token;
	 * @param token The token need to parse;
	 * @return The claim has been parsed;
	 */
	public Claims getClaims(String token) {
		return jwtParser.parseClaimsJws(token).getBody();
	}

	public Authentication getAuthentication(String token) {
		Claims claims = getClaims(token);

		Object authoritiesStr = claims.get(AUTHORITIES_KEY);
		Collection<? extends GrantedAuthority> authorities =
				ObjectUtil.isNotEmpty(authoritiesStr) ?
						Arrays.stream(authoritiesStr.toString().split(","))
								.map(SimpleGrantedAuthority::new)
								.collect(Collectors.toList()) : Collections.emptyList();
		User principal = new User(claims.getSubject(), "*******", authorities);
		return new UsernamePasswordAuthenticationToken(principal, token, authorities);
	}

	/**
	 * Check the token need renew or not;
	 * @param token The token need to detect;
	 */
	public void checkRenewal(String token) {
		// Get the expire time of token.
		OnlineInfo onlineInfo = onlineService.selectInfo(token, true);
		Duration duration = Duration.between(LocalDateTime.now(), onlineInfo.getRemoveTime());
		if (duration.compareTo(Duration.ofDays(CommonConstant.ONLINE_INFO_RENEW_INTERVAL_DAYS)) < 1) {
			onlineService.updateRemoveTime(token);
		}
	}

	/**
	 * Get token from request;
	 * @param request The token extract from;
	 * @return The token has been got;
	 */
	public String getToken(HttpServletRequest request) {
		final String requestHeader = request.getHeader(properties.getHeader());
		if (requestHeader != null && requestHeader.startsWith(properties.getTokenPrefix())) {
			return requestHeader.substring(properties.getTokenPrefix().length());
		}
		return null;
	}
}
