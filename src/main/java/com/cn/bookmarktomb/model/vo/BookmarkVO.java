package com.cn.bookmarktomb.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.cn.bookmarktomb.model.constant.RegularConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author fallen-angle
 */
public class BookmarkVO {

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class InsertBookmarkVO {

		@NotNull
		private String title;

		@NotNull
//		@Pattern(regexp = RegularConstant.URL_REGEXP)
		private String url;

//		@NotNull
		private Long parentId;

		@Null
		@ApiModelProperty(hidden = true)
		private Long ownerId;

		@Pattern(regexp = RegularConstant.URL_REGEXP)
		private String defaultThumbnail;

		@Size(max = 6)
		private Set<String> tags;

		private String description;

		@Getter(AccessLevel.NONE)
		private Boolean top;

		@Past
		private LocalDateTime createdTime;

		public boolean getTop() {
			return top != null && top;
		}

	}

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class InsertBookmarkVOList {

		@Valid
		@NotEmpty
		private List<InsertBookmarkVO> insertBookmarkVOs;

	}

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class GetBookmarkIdVoList {
		@NotEmpty
		private List<Long> getBookmarkIds;
	}

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class DeleteBookmarkVO {

		@NotEmpty
		private List<Long> bookmarkIds;

	}

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class RestoreBookmarkVO {

		@NotEmpty
		private List<Long> bookmarkIds;

	}

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class TopBookmarkVO {

		@NotEmpty
		private List<Long> bookmarkIds;

	}

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class CancelTopBookmarkVO {

		@NotEmpty
		private List<Long> bookmarkIds;

	}

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class ChangeBookmarkVO {

		private Long id;

		@Null
		@ApiModelProperty(hidden = true)
		private Long ownerId;

		private String title;

		private String url;

		private String customThumbnail;

		@Size(max = 6)
		private Set<String> tags;

		private String description;

		private LocalDateTime deleteTime;
	}

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class ChangeParentCollectionVO {

		@Null
		@ApiModelProperty(hidden = true)
		private Long ownerId;

		@NotNull
		private List<Long> ids;

		private Long toParentId;

		private Long fromParentId;

	}

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class SelectByTagVO {

		@Null
		@ApiModelProperty(hidden = true)
		private Long ownerId;

		@NotNull
		private String tag;

	}

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class GetBookmarkVO {

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
	@ApiModel
	@NoArgsConstructor
	public static class SortAndPageVO {

		@NotNull
		@Pattern(regexp = RegularConstant.ORDER_REGEXP)
		String order;

		@NotNull
		@Pattern(regexp = RegularConstant.ACCORDING_REGEXP)
		String according;

		Long offset;

		Integer count;

	}

	private BookmarkVO(){}

	@Override
	public String toString() {
		return "";
	}
}
