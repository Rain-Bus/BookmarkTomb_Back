package com.cn.bookmarktomb.filter;

import com.cn.bookmarktomb.model.cache.ConfigCache;
import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import com.cn.bookmarktomb.model.factory.ApiErrorFactory;
import com.cn.bookmarktomb.model.vo.ApiErrorVO;
import com.cn.bookmarktomb.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author fallen-angle
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InitFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		boolean initFlag = (boolean) ConfigCache.get(ConfigCache.INIT_FLAG);
		if (!initFlag && !isApiInit(request)) {
			ResponseEntity<ApiErrorVO> responseEntity =
					ApiErrorFactory.serverError(ErrorCodeConstant.SYSTEM_NOT_INIT_CODE, ErrorCodeConstant.SYSTEM_NOT_INIT_MSG);
			FilterUtil.generateJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, responseEntity.getBody());
		}
		filterChain.doFilter(request, response);
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
}
