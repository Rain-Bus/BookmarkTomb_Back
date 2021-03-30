package com.cn.bookmarktomb.service.impl;

import cn.hutool.core.map.MapBuilder;
import com.cn.bookmarktomb.excepotion.BadRequestException;
import com.cn.bookmarktomb.model.constant.CommonConstant;
import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import com.cn.bookmarktomb.model.convert.OnlineInfoConvert;
import com.cn.bookmarktomb.model.dto.OnlineInfoDTO.*;
import com.cn.bookmarktomb.model.entity.OnlineInfo;
import com.cn.bookmarktomb.util.MongoUtil;
import com.cn.bookmarktomb.service.OnlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author fallen-angle
 */
@Service
@RequiredArgsConstructor
public class OnlineServiceImpl implements OnlineService {

	private final MongoTemplate mongoTemplate;
	private final OnlineInfoConvert onlineInfoConvert;

	/*-------------------------------------------< Service >----------------------------------------------*/
	// Online info is storage in two Collections(online_info and temp_online_info), if we need, remove documents from the two collections

	@Override
	public void insertUser(InsertOnlineInfoDTO insertOnlineInfoDTO) {
		mongoTemplate.insert(onlineInfoConvert.insertOnlineInfoDTO2DO(insertOnlineInfoDTO));
	}

	@Override
	public void clearUser() {
		Query query = new Query();
		mongoTemplate.remove(query, OnlineInfo.class);
	}

	@Override
	public void deleteById(Long userId) {
		Query query = MongoUtil.getEqQueryByParam("UId", userId);
		mongoTemplate.remove(query, OnlineInfo.class);
	}

	@Override
	public void deleteByToken(String token) {
		Query query = MongoUtil.getEqQueryByParam("_id", token);
		mongoTemplate.remove(query, OnlineInfo.class);
	}

	@Override
	public void updateRemoveTime(String token) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("_id", token)
				.put("Rem", true)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		Update update = getRenewalUpdate();
		mongoTemplate.updateFirst(query, update, OnlineInfo.class);
	}

	@Override
	public OnlineInfo selectInfo(String token, Boolean rememberMe) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("_id", token)
				.put("Rem", Boolean.TRUE.equals(rememberMe))
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		OnlineInfo onlineInfo = mongoTemplate.findOne(query, OnlineInfo.class);
		return Optional.ofNullable(onlineInfo)
				.orElseThrow(() -> new BadRequestException(ErrorCodeConstant.USER_TOKEN_EXPIRED_CODE, ErrorCodeConstant.USER_TOKEN_EXPIRED_MSG));
	}

	@Override
	public List<GetOnlineInfoDTO> listInfos() {
		List<OnlineInfo> onlineInfos = mongoTemplate.findAll(OnlineInfo.class);
		return onlineInfoConvert.getOnlineInfoDO2DTOs(onlineInfos);
	}

	/*----------------------------------------< Generate Update >--------------------------------------*/

	private Update getRenewalUpdate() {
		return new Update()
				.set("ORTm", LocalDateTime.now().plusDays(CommonConstant.ONLINE_INFO_DELETE_INTERVAL_DAYS));
	}

}
