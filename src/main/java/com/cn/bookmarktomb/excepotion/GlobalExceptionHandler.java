package com.cn.bookmarktomb.excepotion;

import com.cn.bookmarktomb.model.entity.Email;
import com.cn.bookmarktomb.util.ThrowableUtil;
import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import com.cn.bookmarktomb.model.factory.ApiErrorFactory;
import com.cn.bookmarktomb.model.vo.ApiErrorVO;
import com.cn.bookmarktomb.util.ThrowableUtil;
import com.sun.mail.util.MailConnectException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handle all exceptions throw from system;
 * @author fallen-angle
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handle the unexpect exceptions;
	 */
	@ExceptionHandler(Throwable.class)
	public ResponseEntity<ApiErrorVO> handleException(Throwable e) {
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.serverError(ErrorCodeConstant.SYSTEM_ERROR_DATA);
	}

	/**
	 * Handle the bad credential when user request;
	 */
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiErrorVO> badCredentialsException(BadCredentialsException e) {
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.requestError(HttpStatus.FORBIDDEN, ErrorCodeConstant.USER_CREDENTIAL_ERROR_CODE, ErrorCodeConstant.USER_CREDENTIAL_ERROR_MSG);
	}

	@ExceptionHandler(MailSendException.class)
	public ResponseEntity<ApiErrorVO> mailHostConnectException(MailSendException e) {
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.serverError(ErrorCodeConstant.EMAIL_UNREACHABLE_CODE, ErrorCodeConstant.EMAIL_UNREACHABLE_MSG);
	}

	@ExceptionHandler(MailAuthenticationException.class)
	public ResponseEntity<ApiErrorVO> mailAuthException(MailAuthenticationException e) {
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.serverError(5007, "Email auth error");
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiErrorVO> badRequestException(BadRequestException e) {
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.requestError(e.getCode(), e.getMessage());
	}

	@ExceptionHandler(EntityExistException.class)
	public ResponseEntity<ApiErrorVO> entityExistException(EntityExistException e) {
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.requestError(e.getCode(), e.getData());
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ApiErrorVO> entityNotFoundException(EntityNotFoundException e) {
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.requestError(e.getCode(), e.getMessage());
	}

	/**
	 * Handle the validator's exception;
	 * to-do: Maybe need more details;
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorVO> methodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.requestError(ErrorCodeConstant.FORM_DATE_ERROR_CODE, ErrorCodeConstant.FORM_DATE_ERROR_MSG);
	}

	@ExceptionHandler(UniqueIdUsedException.class)
	public ResponseEntity<ApiErrorVO> uniqueIdUsedException(UniqueIdUsedException e) {
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.requestError(ErrorCodeConstant.EMAIL_NOT_AUTH_CODE, ErrorCodeConstant.EMAIL_NOT_AUTH_MSG);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorVO> accessDeniedException(AccessDeniedException e) {
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.requestError(ErrorCodeConstant.USER_ACCESS_DENY, ErrorCodeConstant.USER_ACCESS_DENY_MSG);
	}

	@ExceptionHandler(SystemException.class)
	public ResponseEntity<ApiErrorVO> systemError(SystemException e) {
		log.error(getErrorCodeAndMsg(e.getCode(), e.getMessage()));
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.serverError(ErrorCodeConstant.SYSTEM_ERROR_DATA);
	}

	@ExceptionHandler(AccountUnabeledException.class)
	public ResponseEntity<ApiErrorVO> accountUnlabeledException(AccountUnabeledException e) {
		log.error(getErrorCodeAndMsg(e.getCode(), e.getMessage()));
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.requestError(e.getCode(), ErrorCodeConstant.USER_ACCOUNT_NOT_ENABLED_MSG);
	}

	@ExceptionHandler(UnexpectUrlException.class)
	public ResponseEntity<ApiErrorVO> unexpectUrlException(UnexpectUrlException e) {
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.requestError(HttpStatus.NOT_FOUND, ErrorCodeConstant.UNEXPECT_URL_CODE,ErrorCodeConstant.UNEXPECT_URL_MSG);
	}

	@ExceptionHandler(DbOperationException.class)
	public ResponseEntity<ApiErrorVO> dbOperationError(DbOperationException e){
		log.error(getErrorCodeAndMsg(e.getCode(), e.getMessage()));
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.serverError(ErrorCodeConstant.SYSTEM_ERROR_DATA);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiErrorVO> dbOperationError(HttpRequestMethodNotSupportedException e){
		log.error(ThrowableUtil.getStackTrace(e));
		return ApiErrorFactory.requestError(HttpStatus.METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED.value(), e.getMessage());
	}

	private String getErrorCodeAndMsg(int code, String msg) {
		return String.format("errorCode: %5d, errorMsg: %s", code, msg);
	}
}
