package com.cn.bookmarktomb.service;

import com.cn.bookmarktomb.model.dto.OnlineInfoDTO.*;
import com.cn.bookmarktomb.model.entity.OnlineInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fallen-angle
 */
@Service
public interface OnlineService {

	void insertUser(InsertOnlineInfoDTO insertOnlineInfoDTO);

	void clearUser();

	void deleteById(Long userId);

	void deleteByToken(String token);

	void updateRemoveTime(String token);

	OnlineInfo selectInfo(String token, Boolean rememberMe);

	List<GetOnlineInfoDTO> listInfos();

}
