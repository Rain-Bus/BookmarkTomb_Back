package com.cn.bookmarktomb.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;

/**
 * @author fallen-angle
 */
public class SystemInfoVO {

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class GetSystemInfoVO {
		private String osName;

		private String osVersion;

		private String arch;

		private String maxMemory;

		private String currentMemory;

		private String jvmMaxMemory;

		private String jvmCurrentMemory;

		private String javaVersion;

		private String currentDir;

		private String confDir;

		private Long currentPid;

		private String hostIp;

	}

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class EmailVO {

		private String host;

		private Integer port;

		private String username;

		private String password;
	}

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class DatabaseVO {

		String host;

		Integer port;

		String dbname;

		String username;

		String password;

	}

	@Getter
	@Setter
	@ToString
	@NoArgsConstructor
	public static class InitVO {
		@Valid
		EmailVO email;

		@Valid
		DatabaseVO database;

		Integer serverPort;

		Boolean emailEnable;

		Boolean registerEnable;

	}

	private SystemInfoVO(){}

	@Override
	public String toString() {
		return "";
	}

}
