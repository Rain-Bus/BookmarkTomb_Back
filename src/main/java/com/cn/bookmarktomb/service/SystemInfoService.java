package com.cn.bookmarktomb.service;

import com.cn.bookmarktomb.model.dto.SystemInfoDTO.*;
import org.springframework.stereotype.Service;

/**
 * @author fallen-angle
 */
@Service
public interface SystemInfoService {
	GetSystemInfoDTO selectInfo();
}
