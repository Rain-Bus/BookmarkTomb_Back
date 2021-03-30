package com.cn.bookmarktomb.config;

import com.cn.bookmarktomb.model.bean.ProjectProperties;
import com.cn.bookmarktomb.security.bean.JwtLoginProperties;
import com.cn.bookmarktomb.security.bean.JwtSecurityProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fallen-angle
 * Read the configurations form application.yml.
 */
@Configuration
public class SystemConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "jwt")
	public JwtSecurityProperties jwtSecurityProperties(){
		return new JwtSecurityProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = "login")
	public JwtLoginProperties jwtLoginProperties(){
		return new JwtLoginProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = "project")
	public ProjectProperties projectProperties() {
		return new ProjectProperties();
	}

}
