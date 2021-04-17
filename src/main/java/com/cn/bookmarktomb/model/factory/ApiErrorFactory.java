package com.cn.bookmarktomb.model.factory;

import com.cn.bookmarktomb.model.vo.ApiErrorVO;
import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author fallen-angle
 */
public class ApiErrorFactory {

	public static ResponseEntity<ApiErrorVO> serverError(HttpStatus status, Integer code, Object message) {
		return new ResponseEntity<>(new ApiErrorVO(code, message), status);
	}

	public static ResponseEntity<ApiErrorVO> serverError(Integer code, Object message) {
		return new ResponseEntity<>(new ApiErrorVO(code, message), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public static ResponseEntity<ApiErrorVO> serverError(HttpStatus status, Object message) {
		return serverError(status, status.value(), message);
	}

	public static ResponseEntity<ApiErrorVO> serverError(Object message) {
		return serverError(HttpStatus.INTERNAL_SERVER_ERROR, message);
	}

	public static ResponseEntity<ApiErrorVO> serverError() {
		return serverError(ErrorCodeConstant.SYSTEM_ERROR_DATA);
	}

	public static ResponseEntity<ApiErrorVO> requestError(HttpStatus status, Integer code, Object message) {
		return new ResponseEntity<>(new ApiErrorVO(code, message), status);
	}

	public static ResponseEntity<ApiErrorVO> requestError(Integer code, Object message) {
		return requestError(HttpStatus.BAD_REQUEST, code, message);
	}

	public static ResponseEntity<ApiErrorVO> requestError(Object message) {
		return requestError(ErrorCodeConstant.USER_REQUEST_ERROR_CODE, message);
	}

	private ApiErrorFactory() {

	}

}
