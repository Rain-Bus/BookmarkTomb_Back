package com.cn.bookmarktomb.service;

import com.cn.bookmarktomb.model.dto.CodeInfoDTO.*;
import org.springframework.stereotype.Service;

/**
 * @author fallen-angle
 */
@Service
public interface VerifyCodeService {

	void insertImgCode(InsertCodeDTO insertCodeDTO);

	void insertEmailCode(InsertCodeDTO insertCodeDTO);

	void selectResultById(String uuid, String result);
}
