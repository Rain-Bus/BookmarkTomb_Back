package com.cn.bookmarktomb.model.convert;

import com.cn.bookmarktomb.model.dto.SystemInfoDTO.*;
import com.cn.bookmarktomb.model.entity.Database;
import com.cn.bookmarktomb.model.entity.Email;
import com.cn.bookmarktomb.model.vo.SystemInfoVO.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * @author fallen-angle
 */
@Component
@Mapper(componentModel = "spring")
public interface SystemInfoConvert {

	@Mapping(target = "confDir", expression = "java(getSystemInfoDTO.getUserHomeDir() + \"/.bookmark_tomb\")")
	@Mapping(target = "maxMemory", expression =
			"java(com.cn.bookmarktomb.util.CommonUtil.getReadableFileSize(getSystemInfoDTO.getMaxMemory()))")
	@Mapping(target = "currentMemory", expression =
			"java(com.cn.bookmarktomb.util.CommonUtil.getReadableFileSize(getSystemInfoDTO.getCurrentMemory()))")
	@Mapping(target = "jvmMaxMemory", expression =
			"java(com.cn.bookmarktomb.util.CommonUtil.getReadableFileSize(getSystemInfoDTO.getJvmMaxMemory()))")
	@Mapping(target = "jvmCurrentMemory", expression =
			"java(com.cn.bookmarktomb.util.CommonUtil.getReadableFileSize(getSystemInfoDTO.getJvmCurrentMemory()))")
	GetSystemInfoVO getSystemInfoDTO2VO(GetSystemInfoDTO getSystemInfoDTO);

	Email emailVO2DO(EmailVO emailVO);

	EmailVO emailDO2VO(Email email);

	Database databaseVO2DO(DatabaseVO databaseVO);

	DatabaseVO databaseDO2VO(Database database);

}
