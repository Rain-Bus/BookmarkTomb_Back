package com.cn.bookmarktomb.model.convert;

import com.cn.bookmarktomb.model.dto.CodeInfoDTO.*;
import com.cn.bookmarktomb.model.entity.CodeInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * @author fallen-angle
 */
@Component
@Mapper(componentModel = "spring")
public interface CodeInfoConvert {

	@Mapping(target = "removeTime", expression =
			"java(java.time.LocalDateTime.now().plusMinutes(com.cn.bookmarktomb.model.constant.CommonConstant.IMG_CODE_EXPIRE_MINUTES))")
	CodeInfo imgCodeInfoDTO2DO(InsertCodeDTO insertCodeDTO);

	@Mapping(target = "removeTime", expression =
			"java(java.time.LocalDateTime.now().plusMinutes(com.cn.bookmarktomb.model.constant.CommonConstant.EMAIL_CODE_EXPIRE_MINUTES))")
	CodeInfo emailCodeInfoDTO2DO(InsertCodeDTO insertCodeDTO);

}
