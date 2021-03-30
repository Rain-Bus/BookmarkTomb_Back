package com.cn.bookmarktomb.security.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.cn.bookmarktomb.model.vo.UserInfoVO.UserBasicInfoVO;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author This is the implementation of UserDetail;
 */
@Data
@RequiredArgsConstructor
public class JwtUser implements UserDetails {

	private final UserBasicInfoVO userBasicInfoVO;
	private final List<? extends GrantedAuthority> authorities;

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	@JsonIgnore
	public String getPassword() {
		return userBasicInfoVO.getPassword();
	}

	@Override
	public String getUsername() {
		return userBasicInfoVO.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return userBasicInfoVO.getIsEnabled();
	}
}
