package com.cn.bookmarktomb.model.convert;

import com.cn.bookmarktomb.model.dto.OnlineInfoDTO.*;
import com.cn.bookmarktomb.model.entity.OnlineInfo;
import com.cn.bookmarktomb.model.vo.OnlineInfoVO.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fallen-angle
 */
@Component
@Mapper(componentModel = "spring")
public interface OnlineInfoConvert {
	@Mapping(target = "loginTime", expression = "java(java.time.LocalDateTime.now())")
	@Mapping(target = "removeTime", expression =
			"java(insertOnlineInfoDTO.getRememberMe() ? " +
					"java.time.LocalDateTime.now().plusDays(com.cn.bookmarktomb.model.constant.CommonConstant.ONLINE_INFO_DELETE_INTERVAL_DAYS) " +
					": java.time.LocalDateTime.now().plusHours(com.cn.bookmarktomb.model.constant.CommonConstant.TEMP_ONLINE_INFO_DELETE_INTERVAL_HOURS))")
	OnlineInfo insertOnlineInfoDTO2DO(InsertOnlineInfoDTO insertOnlineInfoDTO);

	GetOnlineInfoDTO getOnlineInfoDO2DTO(OnlineInfo onlineInfo);

	List<GetOnlineInfoDTO> getOnlineInfoDO2DTOs(List<OnlineInfo> onlineInfos);

	GetOnlineInfoVO getOnlineInfoDTO2VO(GetOnlineInfoDTO onlineInfoDTO);

	List<GetOnlineInfoVO> getOnlineInfoDTO2VOs(List<GetOnlineInfoDTO> onlineInfoDTO);
}
