package com.cn.bookmarktomb.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * @author fallen-angle
 */
@Data
@Document("code_info")
public class CodeInfo {
	@Id
	private String uid;

	@Field("CRs")
	private String result;

	@Field("CRTm")
	private LocalDateTime removeTime;
}
