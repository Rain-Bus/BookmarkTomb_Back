package com.cn.bookmarktomb.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class SystemInfoDTO {

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class GetSystemInfoDTO {
		private String osName;

		private String osVersion;

		private String arch;

		private Long maxMemory;

		private Long currentMemory;

		private Long jvmMaxMemory;

		private Long jvmCurrentMemory;

		private String javaVersion;

		private String currentDir;

		private String userHomeDir;

		private Long currentPid;

		private String hostIp;

	}

	private SystemInfoDTO(){}

	@Override
	public String toString() {
		return "";
	}

}
