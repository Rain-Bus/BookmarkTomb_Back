package com.cn.bookmarktomb.model.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * @author fallen-angle
 */
@Data
@NoArgsConstructor
@Document(collection = "note_info")
public class Note {

	@Field("NId")
	private Long id;

	@Field("NTit")
	private String title;

	@Field("NOId")
	private Long ownerId;

	@Field("NBId")
	private Long parentId;

	@Field("NCont")
	private String content;

	@Field("NCTm")
	private LocalDateTime createdTime;

	@Field("NRTm")
	private LocalDateTime removeTime;

	@Field("NMTm")
	private LocalDateTime modifyTime;

}