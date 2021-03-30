package com.cn.bookmarktomb.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author fallen-angle
 */
public class ThrowableUtil {

	private ThrowableUtil(){}

	public static String getStackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		try (PrintWriter pw = new PrintWriter(sw)) {
			throwable.printStackTrace(pw);
			return sw.toString();
		}
	}

}
