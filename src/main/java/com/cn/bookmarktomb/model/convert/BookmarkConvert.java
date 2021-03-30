package com.cn.bookmarktomb.model.convert;

import com.cn.bookmarktomb.model.dto.BookmarkDTO.*;
import com.cn.bookmarktomb.model.entity.Bookmark;
import com.cn.bookmarktomb.model.vo.BookmarkVO.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fallen-angle
 */
@Component
@Mapper(componentModel = "spring")
public interface BookmarkConvert {

	@Mapping(target = "modifyTime", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "createdTime",
			expression = "java(java.util.Objects.isNull(insertBookmarkVO.getCreatedTime()) ? java.time.LocalDateTime.now() : insertBookmarkVO.getCreatedTime())")
	@Mapping(target = "topTime",
			expression = "java(insertBookmarkVO.getTop() ? java.time.LocalDateTime.now() : null)")
	@Mapping(target = "tags",
			expression = "java(java.util.Objects.nonNull(insertBookmarkVO.getTags()) && insertBookmarkVO.getTags().size()>0 ? insertBookmarkVO.getTags() : null)")
	InsertBookmarkDTO insertBookmarkVO2DTO(InsertBookmarkVO insertBookmarkVO);

	Bookmark insertBookmarkDTO2DO(InsertBookmarkDTO insertBookmarkDTO);

	@Mapping(target = "order", expression = "java(org.springframework.data.domain.Sort.Direction.fromString(sortAndPageVO.getOrder()))")
	@Mapping(target = "according", expression = "java(com.cn.bookmarktomb.model.enums.BookmarkOrderFieldEnum.valueOf(sortAndPageVO.getAccording().toUpperCase()).getFields())")
	@Mapping(target = "offset", expression = "java(java.util.Optional.ofNullable(sortAndPageVO.getOffset()).orElse(0L))")
	@Mapping(target = "count", expression = "java(java.util.Optional.ofNullable(sortAndPageVO.getCount()).orElse(Integer.MAX_VALUE))")
	SortAndPageDTO sortAndPageVO2DTO(SortAndPageVO sortAndPageVO);

	List<InsertBookmarkDTO> insertBookmarkVO2DTOs(List<InsertBookmarkVO> insertBookmarkVOs);

	List<Bookmark> insertBookmarkDTO2DOs(List<InsertBookmarkDTO> insertBookmarkDTOs);

	@Mapping(target = "modifyTime", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "tags",
			expression = "java(changeBookmarkVO.getTags()!=null && changeBookmarkVO.getTags().size()>0 ? changeBookmarkVO.getTags() : null)")
	ChangeBookmarkDTO changeBookmarkVO2DTO(ChangeBookmarkVO changeBookmarkVO);

	@Mapping(target = "modifyTime", expression = "java(java.time.LocalDateTime.now())")
	ChangeParentCollectionDTO changeParentCollectionVO2DTO(ChangeParentCollectionVO changeParentCollectionVO);

	GetBookmarkDTO getBookmarkDO2DTO(Bookmark bookmark);

	GetBookmarkDTO insert2GetBookmarkDTO(InsertBookmarkDTO insertBookmarkDTO);

	GetBookmarkVO getBookmarkDTO2VO(GetBookmarkDTO getBookmarkDTO);

	List<GetBookmarkDTO> getBookmarkDO2DTOs(List<Bookmark> bookmarks);

	List<GetBookmarkVO> getBookmarkDTO2VOs(List<GetBookmarkDTO> getBookmarkDTOS);

	List<GetBookmarkDTO> insert2GetBookmarkDTOs(List<InsertBookmarkDTO> insertBookmarkDTOs);

}