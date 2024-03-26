package com.example.springSecurity.entity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.RequiredArgsConstructor;

// Spring Security가 로그인 POST 요청을 받아 로그인을 진행시킴 
// 로컬 로그인 - UserDetails 구현
// 소셜 로그인 - OAuth2User 구현
public class MyUserDetails implements UserDetails, OAuth2User {
	
	private SecurityUser securityUser; // 스프링이 생성자 방식으로 생성자 주입해줌
	private Map<String, Object> attributes;
	
	/*
	 * 생성자 
	 */
	public MyUserDetails() { } 
	
	// 로컬 로그인 - 스프링이 생성자 방식으로 의존성 주입 
	public MyUserDetails(SecurityUser securityUser) {
		this.securityUser = securityUser;
	} 
	
	// 소셜 로그인 - 스프링이 생성자 방식으로 의존성 주입 
	public MyUserDetails(SecurityUser securityUser, Map<String, Object> attributes) {
		this.securityUser = securityUser;
		this.attributes = attributes;
	} 
	
	
	/*
	 *  해당 사용자의 권한을 리턴 ex) 관리자냐 사용자냐 등 
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() { // GrantedAuthority를 상속받은 어떤 것이든 가능 
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new GrantedAuthority() { // GrantedAuthority 클래스를 만들고 메서드도 오버라이드 		
			@Override
			public String getAuthority() {
				return securityUser.getRole(); // 권한 넘겨주기 
			}
		});
		return collect;
	}

	/*
	 *  UserDetails 구현부분
	 */
	@Override
	public String getPassword() {
		return securityUser.getPwd();
	}

	@Override
	public String getUsername() { // 스프링에선 id를 userName라고 부름
		return securityUser.getUid();
	}

	@Override
	public boolean isAccountNonExpired() {
		if (securityUser.getIsDeleted() == 0) {
			return true;
		}
		return false;
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
		return true;
	}
	
	/*
	 * OAuth2User 구현부분
	 */
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	@Override
	public String getName() {
		return null;
	}
	
	public SecurityUser getSecurityUser() {
		return securityUser;
	}
}
