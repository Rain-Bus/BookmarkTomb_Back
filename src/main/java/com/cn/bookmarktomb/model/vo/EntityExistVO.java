package com.cn.bookmarktomb.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fallen-angle
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityExistVO {
	private String conflictName;
	private Object clientConflict;
	private Object serverConflict;
}
