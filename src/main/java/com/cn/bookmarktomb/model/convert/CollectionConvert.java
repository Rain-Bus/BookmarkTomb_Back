package com.cn.bookmarktomb.model.convert;

import com.cn.bookmarktomb.model.dto.CollectionDTO.*;
import com.cn.bookmarktomb.model.entity.Collection;
import com.cn.bookmarktomb.model.vo.CollectionVO.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fallen-angle
 */
@Component
@Mapper(componentModel = "spring")
public interface CollectionConvert {

	Collection insertCollectionDTO2DO(InsertCollectionDTO insertCollectionDTO);

	@Mapping(target = "modifyTime", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "createdTime",
			expression = "java(java.util.Objects.isNull(insertCollectionVO.getCreatedTime()) ? java.time.LocalDateTime.now() : insertCollectionVO.getCreatedTime())")
	InsertCollectionDTO insertCollectionVO2DTO(InsertCollectionVO insertCollectionVO);

	List<Collection> insertCollectionDTO2DOs(List<InsertCollectionDTO> insertCollectionDTOs);

	List<InsertCollectionDTO> insertCollectionVO2DTOs(List<InsertCollectionVO> insertCollectionVOs);

	GetCollectionDTO getCollectionDO2DTO(Collection collection);

	@Mapping(target = "item", expression = "java(0)")
	GetCollectionDTO insert2GetCollectionDTO(InsertCollectionDTO insertCollectionDTO);

	GetCollectionVO getCollectionDTO2VO(GetCollectionDTO getCollectionDTOs);

	List<GetCollectionDTO> getCollectionDO2DTOs(List<Collection> collection);

	List<GetCollectionVO> getCollectionDTO2VOs(List<GetCollectionDTO> getCollectionDTOs);

	@Mapping(target = "modifyTime", expression = "java(java.time.LocalDateTime.now())")
	CollectionCommonInfoDTO collectionCommonInfoVO2DTO(CollectionCommonInfoVO collectionCommonInfoVO);

	Collection collectionCommonInfoDTO2DO(CollectionCommonInfoDTO collectionCommonInfoDTO);

	@Mapping(target = "modifyTime", expression = "java(java.time.LocalDateTime.now())")
	ChangeParentDTO changeParentVO2DTO(ChangeParentVO changeParentVO);

}
