package com.cn.bookmarktomb.model.entity;

import lombok.Data;

/**
 * @author fallen-angle
 */
@Data
public class Database {

	String host;

	Integer port;

	String dbname;

	String username;

	String password;

	public String getDbname() {
		return dbname == null ? "bookmark_tomb" : dbname;
	}
}
