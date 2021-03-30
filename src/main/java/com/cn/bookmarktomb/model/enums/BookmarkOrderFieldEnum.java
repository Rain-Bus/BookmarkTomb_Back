package com.cn.bookmarktomb.model.enums;

/**
 * @author fallen-angle
 */
public enum BookmarkOrderFieldEnum {
	// According to create time
	CREATE("creat", "BCTm"),

	// According to lase edit time
	EDIT("edit", "BETm"),

	// According to top time and created time
	DEFAULTS("defaults","BTTm", "BCTm");


	private final String name;
	private final String[] fields;

	BookmarkOrderFieldEnum(String name, String... fields) {
		this.name = name;
		this.fields = fields;
	}

	public String[] getFields() {
		return this.fields;
	}
}
