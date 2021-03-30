package com.cn.bookmarktomb.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.cn.bookmarktomb.model.constant.RegularConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fallen-angle
 */
public class CollectionVO {

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class InsertCollectionVO {

		private Long id;

		private Long parentId;

		@ApiModelProperty(hidden = true)
        @Null(message = "This filed must be null!")
		private Long ownerId;

		private Long serverParentId;

		@NotNull
		@Pattern(regexp = RegularConstant.COLLECTION_NAME_REGEXP, message = "Collection name can be composed by _ , @ and chinese or english characters!")
		private String title;

		@Past
		private LocalDateTime createdTime;

		@Size(max = 50, message = "The description's length must less than 50!")
		private String description;

	}

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class InsertCollectionVOList {
		@Valid
		private List<InsertCollectionVO> insertCollectionVOs;
	}

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class CollectionCommonInfoVO {

		@ApiModelProperty(hidden = true)
        @Null(message = "This filed must be null!")
		private Long ownerId;

		@NotNull(message = "This filed can't be null!")
		private Long id;

		@NotNull(message = "This filed can't be null!")
		@Pattern(regexp = RegularConstant.COLLECTION_NAME_REGEXP, message = "Collection name can be composed by _ , @ and chinese or english characters!")
		private String title;

		@Future(message = "This must be a future time!")
		private LocalDateTime deleteTime;

		@Size(max = 50, message = "The description's length must less than 50!")
		private String description;

	}

	@Data
	@ApiModel
	@NoArgsConstructor
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class GetCollectionVO {

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
	@ApiModel
	@Deprecated
	@NoArgsConstructor
	public static class ChangeVisitStatusVO {

		@NotNull(message = "This filed can't be null!")
		private Long id;

		@ApiModelProperty(hidden = true)
        @Null(message = "This filed must be null!")
		private Long ownerId;

		private List<Long> ownerTeamIds;

	}

	@Getter
	@Setter
	@ToString
	@ApiModel
	@NoArgsConstructor
	public static class ChangeParentVO {

		@ApiModelProperty(hidden = true)
        @Null(message = "This filed must be null!")
		private Long ownerId;

		@NotNull(message = "This filed can't be null!")
		private Long id;

		@NotNull(message = "This filed can't be null!")
		private Long toParentId;

		@NotNull(message = "This filed can't be null!")
		private Long fromParentId;

	}

	private CollectionVO(){}

	@Override
	public String toString() {
		return "";
	}
}