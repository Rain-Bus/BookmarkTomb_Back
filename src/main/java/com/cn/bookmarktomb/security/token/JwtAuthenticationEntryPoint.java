package com.cn.bookmarktomb.security.token;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author fallen-angle
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
