package com.cn.bookmarktomb.security.token;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.util.StrUtil;
import com.cn.bookmarktomb.excepotion.BadRequestException;
import com.cn.bookmarktomb.model.cache.ConfigCache;
import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import com.cn.bookmarktomb.model.factory.ApiErrorFactory;
import com.cn.bookmarktomb.model.vo.ApiErrorVO;
import com.cn.bookmarktomb.service.OnlineService;
import com.cn.bookmarktomb.util.JsonUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author fallen-angle
 * This is the filter of every request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

	private final TokenProvider tokenProvider;
	private final OnlineService onlineService;

	private static final String USER_ID_KEY = "userId";
	private static final String USER_TOKEN = "userToken";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		boolean initFlag = (boolean) ConfigCache.get(ConfigCache.INIT_FLAG);
		if (!initFlag && !isApiInit(request)) {
			ResponseEntity<ApiErrorVO> responseEntity = ApiErrorFactory.serverError("System not init!");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("application/json");
			OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
			PrintWriter printWriter = new PrintWriter(osw, true);
			ApiErrorVO apiErrorVO = responseEntity.getBody();
			printWriter.print(JsonUtil.mapToJson(apiErrorVO));
			printWriter.close();
			osw.close();
			filterChain.doFilter(request, response);
			return;
		} else if (!initFlag && isApiInit(request)) {
			filterChain.doFilter(request, response);
			return;
		}
		boolean adminFlag = (boolean) ConfigCache.get(ConfigCache.ADMIN_FLAG);
		if (!adminFlag && !isAPiInitAdmin(request)) {
			ResponseEntity<ApiErrorVO> responseEntity = ApiErrorFactory.serverError("Admin account hasn't been set!");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("application/json");
			OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
			PrintWriter printWriter = new PrintWriter(osw, true);
			ApiErrorVO apiErrorVO = responseEntity.getBody();
			printWriter.print(JsonUtil.mapToJson(apiErrorVO));
			printWriter.close();
			osw.close();
			filterChain.doFilter(request, response);
			return;
		} else if (!adminFlag && isAPiInitAdmin(request)) {
			filterChain.doFilter(request, response);
			return;
		}
		String authToken = tokenProvider.getToken(request);
		boolean rememberMe = getRememberMe(request);
		// If don't request have token, the request will pass directly.
		if (!isApiPublic(request) && StrUtil.isNotBlank(authToken)) {
			try {
				onlineService.selectInfo(authToken, rememberMe);
				if (rememberMe) {
					tokenProvider.checkRenewal(authToken);
				}
			} catch (BadRequestException e) {
				ResponseEntity<ApiErrorVO> responseEntity = ApiErrorFactory.requestError(ErrorCodeConstant.USER_TOKEN_EXPIRED_CODE, e.getMessage());
				/*
				If catch this exception will return a json instead of the servlet html.
					The setStatus must be set before the printWriter close or flush,
					due to these operation will invoke the function OnCommittedResponseWrapper.doOnResponseCommitted() to change the status of commit,
					which will prevent the change of response, the implementation can refer to ResponseFacade.setStatus(int sc).
					The implementation detail can be found by debug.
				 */
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.setContentType("application/json");
				OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
				PrintWriter printWriter = new PrintWriter(osw, true);
				ApiErrorVO apiErrorVO = responseEntity.getBody();
				printWriter.print(JsonUtil.mapToJson(apiErrorVO));
				printWriter.close();
				osw.close();
				filterChain.doFilter(request, response);
				return;
			}

			Claims claims = tokenProvider.getClaims(authToken);

			// Read role and username infos from token, then set it to Context
			String userName = (String)claims.get("username");
			boolean isAdmin = (boolean)claims.get("isAdmin");
			List<GrantedAuthority> authorities = new ArrayList<>(1);
			authorities.add(new SimpleGrantedAuthority(isAdmin ? "ROLE_ADMIN" : "ROLE_USER"));
			SecurityContextHolder.clearContext();
			SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userName, null, authorities));

			// Read the user id and write into request param, the controller can read this by param instead of get from request.
			String userId = claims.getSubject();
			HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request) {
				@Override
				public Enumeration<String> getParameterNames() {
					Set<String> paramNames = new LinkedHashSet<>();
					paramNames.add(USER_ID_KEY);
					paramNames.add(USER_TOKEN);
					Enumeration<String> names =  super.getParameterNames();
					while(names.hasMoreElements()) {
						paramNames.add(names.nextElement());
					}
					return Collections.enumeration(paramNames);
				}

				@Override
				public String[] getParameterValues(String name) {
					if (USER_ID_KEY.equals(name)) {
						return new String[]{userId};
					}
					else if (USER_TOKEN.equals(name)) {
						return new String[]{authToken};
					}
					return super.getParameterValues(name);
				}
			};
			filterChain.doFilter(requestWrapper, response);
		} else {
			filterChain.doFilter(request, response);
		}
	}

	private boolean getRememberMe(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader("rememberMe"))
				.map(Boolean::valueOf)
				.orElse(false);
	}

	static boolean isApiPublic(HttpServletRequest request) {
		final String get = String.valueOf(RequestMethod.GET);
		final String post = String.valueOf(RequestMethod.POST);
		Map<String, String> publicMap = MapBuilder.<String, String>create()
				.put("^/api/public/system$", get)
				.put("^/api/public/init$", post)
				.put("^/api/code/.*$", get)
				.put("^/api/doc.html", get)
				.put("^/api/user/.*$", post)
				.map();
		for (Map.Entry<String, String> entry: publicMap.entrySet()) {
			if (request.getRequestURI().matches(entry.getKey()) && entry.getValue().equals(request.getMethod())){
				return true;
			}
		}
		return false;
	}

	private boolean isApiInit(HttpServletRequest request) {
		List<String> initApi = List.of("^/webjar.*$", "^/api/public/init$", "^/doc.html.*$", "^/swagger-resources$", "^/v3/api-docs$");
		for (String url: initApi) {
			if (request.getRequestURI().matches(url)) {
				return true;
			}
		}
		return false;
	}

	private boolean isAPiInitAdmin(HttpServletRequest request) {
		List<String> initApi = List.of("^/webjar.*$", "^/api/public/admin$", "^/doc.html.*$", "^/swagger-resources$", "^/v3/api-docs$");
		for (String url: initApi) {
			if (request.getRequestURI().matches(url)) {
				return true;
			}
		}
		return false;
	}

}