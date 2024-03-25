package com.example.springSecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

@Configuration // @을 통해 pom.xml을 자바 코드로 불러옴
@EnableWebSecurity
public class SecurityConfig {

	@Bean // 주입 가능하게 함
	public DefaultSecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(auto -> auto.disable()) 	// 괄호 안에 람다함수를 사용해야함, csrf:보안 관련
											// build 패턴과 유사하게 . . . 으로 간다
			.headers(x -> x.frameOptions(y -> y.disable()))		// CK Editor image upload
		;
		
		return http.build();
	}
}
