package com.cn.bookmarktomb.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author fallen-angle
 */
@Data
public class ApiErrorVO {

	private Integer code;
	private Object message;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp;

	public ApiErrorVO(Integer code, Object message) {
		this.code = code;
		this.message = message;
		this.timestamp = LocalDateTime.now();
	}
}
