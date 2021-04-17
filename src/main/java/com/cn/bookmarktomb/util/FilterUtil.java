package com.cn.bookmarktomb.util;

import com.cn.bookmarktomb.model.vo.ApiErrorVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class FilterUtil {
	public static void generateJsonResponse(HttpServletResponse response, int status, ApiErrorVO errorInfo) throws IOException {
		response.setStatus(status);
		response.setContentType("application/json");
		OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
		PrintWriter printWriter = new PrintWriter(osw, true);
		printWriter.print(JsonUtil.mapToJson(errorInfo));
		printWriter.close();
		osw.close();
	}
}