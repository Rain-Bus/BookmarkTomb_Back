package com.cn.bookmarktomb.config;

import com.cn.bookmarktomb.filter.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class FilterOrderConfig {

	private final InitFilter initFilter;
	private final AdminFilter adminFilter;
	private final DatabaseFilter databaseFilter;
	private final TokenFilter tokenFilter;
	private final StartFilter startFilter;

	@Bean
	public FilterRegistrationBean<InitFilter> filterInit() {
		FilterRegistrationBean<InitFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(initFilter);
		registration.addUrlPatterns("/*");
		registration.setOrder(Integer.MAX_VALUE-4);
		return registration;
	}

	@Bean
	public FilterRegistrationBean<StartFilter> filterStart() {
		FilterRegistrationBean<StartFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(startFilter);
		registration.addUrlPatterns("/*");
		registration.setOrder(Integer.MAX_VALUE-3);
		return registration;
	}
	
	@Bean
	public FilterRegistrationBean<DatabaseFilter> filterDb() {
		FilterRegistrationBean<DatabaseFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(databaseFilter);
		registration.addUrlPatterns("/*");
		registration.setOrder(Integer.MAX_VALUE-2);
		return registration;
	}
	
	@Bean
	public FilterRegistrationBean<AdminFilter> filterAdmin() {
		FilterRegistrationBean<AdminFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(adminFilter);
		registration.addUrlPatterns("/*");
		registration.setOrder(Integer.MAX_VALUE-1);
		return registration;
	}
	
	@Bean
	public FilterRegistrationBean<TokenFilter> filterToken() {
		FilterRegistrationBean<TokenFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(tokenFilter);
		registration.addUrlPatterns("/*");
		registration.setOrder(Integer.MAX_VALUE);
		return registration;
	}

}
