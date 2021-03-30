package com.cn.bookmarktomb.model.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @author fallen-angle
 */
@Data
@NoArgsConstructor
@Document(collection = "public_book_collection_info")
public class PublicCollection {

	@Id
	private Long collectionPublicId;

	@Field("PCId")
	private Long parentCollectionId;

	@Field("CSId")
	private Long collectionSourceId;

	@Field("CCId")
	private Long collectionCreatorId;

	@Field("COTId")
	private Long collectionOwnerTeamId;

	@Field("CName")
	private String collectionName;

	@Field("CPTm")
	private LocalDateTime collectionPublicTime;

	@Field("CUTm")
	private LocalDateTime collectionLastUpdateTime;

	@Field("CDsc")
	private String collectionDescription;

	@Field("CAva")
	private String collectionAvatarUri;

	@Field("CClo")
	private Integer collectionCloned;

	@Field("CLik")
	private Integer collectionLiked;

	@Field("CViw")
	private Integer collectionViewed;

	@Field("CTag")
	private ArrayList<String> collectionTags;
}
