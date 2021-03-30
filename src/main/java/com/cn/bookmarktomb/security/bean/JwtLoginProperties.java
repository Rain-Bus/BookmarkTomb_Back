package com.cn.bookmarktomb.security.bean;

import cn.hutool.core.util.StrUtil;
import com.cn.bookmarktomb.excepotion.BadConfigurationException;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.Data;

import java.awt.*;
import java.util.Objects;

/**
 * This is used to get the properties in spring configuration;
 * @author fallen-angle
 */
@Data
public class JwtLoginProperties {

	private LoginCode loginCode = null;

	private Boolean cacheEnable = null;

	public boolean isCacheEnable() {
		return cacheEnable;
	}

	public Captcha getCaptcha() {
		if (Objects.isNull(loginCode)) {
			loginCode = new LoginCode();
			if (Objects.isNull(loginCode.getCodeType())) {
				loginCode.setCodeType(LoginCodeEnum.ARITHMETIC);
			}
		}
		return switchCaptcha(loginCode);
	}

	private Captcha switchCaptcha(LoginCode loginCode) {
		Captcha captcha;
		synchronized (this) {
			switch (loginCode.getCodeType()) {
				case ARITHMETIC:
					captcha = new ArithmeticCaptcha(loginCode.getCodeImageWidth(), loginCode.getCodeImageHeight());
					captcha.setLen(loginCode.getCodeLength());
					break;
				case CHINESE:
					captcha = new ChineseCaptcha(loginCode.getCodeImageWidth(), loginCode.getCodeImageHeight());
					captcha.setLen(loginCode.getCodeLength());
					break;
				case GIF:
					captcha = new GifCaptcha(loginCode.getCodeImageWidth(), loginCode.getCodeImageHeight());
					captcha.setLen(loginCode.getCodeLength());
					break;
				default:
					throw new  BadConfigurationException("Captcha setting configuration error!");
			}
		}
		if (!StrUtil.isBlank(loginCode.getCodeFontName())) {
			captcha.setFont(new Font(loginCode.getCodeFontName(), Font.PLAIN, loginCode.getCodeFontSize()));
		}
		return captcha;
	}

}
