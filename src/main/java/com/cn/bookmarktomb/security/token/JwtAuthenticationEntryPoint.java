package com.cn.bookmarktomb.security.token;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * When user access RestAPI without any certificate, this will send an 401 error code;
 * @author fallen-angle
 * @date 2020-08-18 11:42
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request,
						 HttpServletResponse response,
						 AuthenticationException authenticationException) throws IOException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException == null?"Unauthorized": authenticationException.getMessage());

	}
}
