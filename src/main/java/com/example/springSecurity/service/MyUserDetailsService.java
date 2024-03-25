package com.example.springSecurity.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.springSecurity.entity.MyUserDetails;
import com.example.springSecurity.entity.SecurityUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
// 구현 객체를 만들면 스프링 시큐리티가 의존성 주입해줌(IOC)
public class MyUserDetailsService implements UserDetailsService{
    private final SecurityUserService securityService;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		SecurityUser securityUser = securityService.getUserByUid(username);
		
		if(securityUser != null) {
			log.info("login 완료: " + securityUser.getUid());
			return new MyUserDetails(securityUser);
		}
		return null;
	}
	
}
