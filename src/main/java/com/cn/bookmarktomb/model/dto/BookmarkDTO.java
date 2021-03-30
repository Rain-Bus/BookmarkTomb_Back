package com.cn.bookmarktomb.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Sort.Direction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author fallen-angle
 * */
public class BookmarkDTO {

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class InsertBookmarkDTO {

		private Long id;

		private Long parentId;

		private Long ownerId;

		private String title;

		private String url;

		private String defaultThumbnail;

		private Set<String> tags;

		private String description;

		private LocalDateTime topTime;

		private LocalDateTime modifyTime;

		private LocalDateTime deleteTime;

		private LocalDateTime createdTime;

	}

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class ChangeBookmarkDTO {

		private Long id;

		private Long ownerId;

		private String title;

		private String url;

		private String customThumbnail;

		private Set<String> tags;

		private String description;

		private LocalDateTime modifyTime;

		private LocalDateTime deleteTime;

	}

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class ChangeParentCollectionDTO {

		private Long ownerId;

		private List<Long> ids;

		private Long fromParentId;

		private Long toParentId;

		private LocalDateTime modifyTime;

	}

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class GetBookmarkDTO {

		private Long id;

		private Long parentId;

		private String title;

		private String url;

		private String defaultThumbnail;

		private String customThumbnail;

		private Set<String> tags;

		private LocalDateTime topTime;

		private LocalDateTime modifyTime;

		private LocalDateTime deleteTime;

		private LocalDateTime removeTime;

		private LocalDateTime createdTime;

	}

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class SortAndPageDTO {

		Direction order;

		String[] according;

		Long offset;

		Integer count;

	}

	private BookmarkDTO(){}

	@Override
	public String toString() {
		return "";
	}

}
