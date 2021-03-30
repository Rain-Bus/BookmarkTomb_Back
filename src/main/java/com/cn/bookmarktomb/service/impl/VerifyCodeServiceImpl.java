package com.cn.bookmarktomb.service.impl;

import cn.hutool.core.map.MapBuilder;
import com.cn.bookmarktomb.excepotion.BadRequestException;
import com.cn.bookmarktomb.model.constant.ErrorCodeConstant;
import com.cn.bookmarktomb.model.convert.CodeInfoConvert;
import com.cn.bookmarktomb.model.dto.CodeInfoDTO.*;
import com.cn.bookmarktomb.model.entity.CodeInfo;
import com.cn.bookmarktomb.util.MongoUtil;
import com.cn.bookmarktomb.service.VerifyCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * @author fallen-angle
 */

@Service
@RequiredArgsConstructor
public class VerifyCodeServiceImpl implements VerifyCodeService {

	private final MongoTemplate mongoTemplate;
	private final CodeInfoConvert codeInfoConvert;

	/*-------------------------------------------< Service >----------------------------------------------*/

	@Override
	public void insertImgCode(InsertCodeDTO insertCodeDTO) {
		CodeInfo codeInfo = codeInfoConvert.imgCodeInfoDTO2DO(insertCodeDTO);
		mongoTemplate.insert(codeInfo);
	}

	@Override
	public void insertEmailCode(InsertCodeDTO insertCodeDTO) {
		Query query = MongoUtil.getEqQueryByParam("_id", insertCodeDTO.getUid());
		Update update = getEmailCodeUpdate(codeInfoConvert.emailCodeInfoDTO2DO(insertCodeDTO));
		mongoTemplate.upsert(query, update, CodeInfo.class);
	}

	@Override
	public void selectResultById(String id, String result) {
		Map<String, Object> queryMap = MapBuilder.<String, Object>create()
				.put("_id", id)
				.put("CRs", result)
				.map();
		Query query = MongoUtil.getEqQueryByMap(queryMap);
		if (Objects.isNull(mongoTemplate.findAndRemove(query, CodeInfo.class))) {
			throw new BadRequestException(ErrorCodeConstant.USER_VERIFY_ERROR_CODE, "Verify code expired or incorrect!");
		}
	}

	/*-------------------------------------------< Generate Updates >----------------------------------------------*/

	private Update getEmailCodeUpdate(CodeInfo codeInfo) {
		return new Update()
				.set("_id", codeInfo.getUid())
				.set("CRs", codeInfo.getResult())
				.set("CRTm", codeInfo.getRemoveTime())
				.set("_class", codeInfo.getClass().toString());
	}
}
