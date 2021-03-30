package com.cn.bookmarktomb.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author homeubuntu
 */
public class CollectionDTO {

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
    public static class InsertCollectionDTO {

		private Long id;

		private Long parentId;

		private Long serverParentId;

		private Long ownerId;

		private String title;

		private LocalDateTime deleteTime;

		private String description;

		private LocalDateTime createdTime;

		private LocalDateTime modifyTime;

	}

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class GetCollectionDTO {

		private Long id;

		private Long parentId;

		private String title;

		private LocalDateTime createdTime;

		private LocalDateTime deleteTime;

		private LocalDateTime removeTime;

		private LocalDateTime modifyTime;

		private String description;

		private ArrayList<String> tags;

		private Integer item;

	}

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class CollectionCommonInfoDTO {

		private Long ownerId;

		private Long id;

		private String title;

		private LocalDateTime deleteTime;

		private String description;

		private LocalDateTime modifyTime;

	}

	@Getter
	@Setter
	@ToString
	@Deprecated
	@NoArgsConstructor
	public static class ChangeVisitStatusDTO {

		private Long id;

		private Long ownerId;

		private List<Long> collectionOwnerTeamIds;

		private LocalDateTime modifyTime;

	}

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class ChangeParentDTO {

		private Long ownerId;

		private Long id;

		private Long toParentId;

		private Long fromParentId;

		private LocalDateTime modifyTime;

	}

	private CollectionDTO(){}

	@Override
	public String toString() {
		return "";
	}
}
