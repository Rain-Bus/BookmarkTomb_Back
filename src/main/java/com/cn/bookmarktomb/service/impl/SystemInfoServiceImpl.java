package com.cn.bookmarktomb.service.impl;

import cn.hutool.system.SystemUtil;
import com.cn.bookmarktomb.model.dto.SystemInfoDTO.*;
import com.cn.bookmarktomb.service.SystemInfoService;
import com.cn.bookmarktomb.util.CommonUtil;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

/**
 * @author fallen-angle
 */
@Service
public class SystemInfoServiceImpl implements SystemInfoService {

	@Override
	public GetSystemInfoDTO selectInfo() {
		SystemInfo systemInfo = new SystemInfo();
		OperatingSystem os = systemInfo.getOperatingSystem();
		HardwareAbstractionLayer hardware = systemInfo.getHardware();

		GetSystemInfoDTO getSystemInfoDTO = new GetSystemInfoDTO();
		getSystemInfoDTO.setOsName(os.getFamily());
		getSystemInfoDTO.setArch(SystemUtil.getOsInfo().getArch());
		getSystemInfoDTO.setOsVersion(SystemUtil.getOsInfo().getVersion());
		getSystemInfoDTO.setMaxMemory(hardware.getMemory().getTotal());
		getSystemInfoDTO.setCurrentMemory(hardware.getMemory().getTotal() - hardware.getMemory().getAvailable());
		getSystemInfoDTO.setJavaVersion(SystemUtil.getJavaInfo().getVersion());
		getSystemInfoDTO.setJvmMaxMemory(SystemUtil.getMaxMemory());
		getSystemInfoDTO.setJvmCurrentMemory(SystemUtil.getTotalMemory());
		getSystemInfoDTO.setCurrentDir(SystemUtil.getUserInfo().getCurrentDir());
		getSystemInfoDTO.setUserHomeDir(SystemUtil.getUserInfo().getHomeDir());
		getSystemInfoDTO.setCurrentPid(SystemUtil.getCurrentPID());
		getSystemInfoDTO.setHostIp(CommonUtil.getHostPublicIp());
		return getSystemInfoDTO;
	}
}
