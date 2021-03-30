package com.cn.bookmarktomb.model.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author homeubuntu
 */
@Data
@NoArgsConstructor
@Document(collection = "bookmark_info")
public class Bookmark {

	@Field("BId")
	private Long id;

	@Field("BCId")
	private Long parentId;

	@Field("BOId")
	private Long ownerId;

	@Field("BTit")
	private String title;

	@Field("BUrl")
	private String url;

	@Field("BDThn")
	private String defaultThumbnail;

	@Field("BCThn")
	private String customThumbnail;

	@Field("BTag")
	private Set<String> tags;

	@Field("BDsc")
	private String description;

	@Field("BViw")
	private Integer bookmarkViewed;

	@Field("BTTm")
	private LocalDateTime topTime;

	@Field("BMTm")
	private LocalDateTime modifyTime;

	@Field("BRTm")
	private LocalDateTime removeTime;

	@Field("BDTm")
	private LocalDateTime deleteTime;

	@Field("BCTm")
	private LocalDateTime createdTime;

}
