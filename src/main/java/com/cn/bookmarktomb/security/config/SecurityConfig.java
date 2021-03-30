package com.cn.bookmarktomb.security.config;

import com.cn.bookmarktomb.security.token.JwtAccessDeniedHandler;
import com.cn.bookmarktomb.security.token.JwtAuthenticationEntryPoint;
import com.cn.bookmarktomb.security.token.TokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

/**
 * @author fallen-angle
 * This is the configration of spring security;
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final CorsFilter corsFilter;
	private final TokenFilter tokenFilter;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
				.addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
				// Handle the errors of auth.
				.exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler)
				// Handle the iframe CORS.
				.and()
				.headers().frameOptions().disable()
				// Don't create session, because the token authentication don't need session.
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				// Set the URLs' access permission.
				.and().authorizeRequests()
				// The access permission of static resources.
				.antMatchers(
						HttpMethod.GET,
						"/*.html",
						"/**/*.html",
						"/**/*.css",
						"/**/*.js"
				).permitAll()
				// The access permission of avatar.
				.antMatchers("/avatar/**").permitAll()
				// The option request when CORS.
				.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				// Allow all request of anonymous.
				// Any Request are all need authentication.
				.anyRequest().permitAll();
	}

	@Bean
	public PasswordEncoder encoder(){
		return new BCryptPasswordEncoder();
	}

}
